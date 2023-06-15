package com.elazarhalperin.fluentify.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.elazarhalperin.fluentify.Models.ChatModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.UserTypeHelper;
import com.elazarhalperin.fluentify.helpers.adapters.MessagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    TextView tv_chatName;
    EditText et_message;
    FloatingActionButton fab_sendTheMessage, fab_goBack;
    ProgressBar progressBar;

    RecyclerView rv_messages;
    MessagesAdapter adapter;
    List<Map<String, Object>> messages;
    String shtok;
    String shtok2;
    String userType;

    String chatRoomId;
    String messageTo;
    String chatName;
    private ListenerRegistration chatRoomListener;

    FirebaseUser firebaseUser;
    UserTypeHelper userTypeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userTypeHelper = new UserTypeHelper(getApplicationContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatRoomId = getIntent().getStringExtra("chatRoomId");

        userType = userTypeHelper.getUserType();
        messageTo = getIntent().getStringExtra("messageTo");
        shtok = userType.equals("student") ? firebaseUser.getUid() : messageTo;
        shtok2 = userType.equals("teacher") ? firebaseUser.getUid() : messageTo;
        chatName = getIntent().getStringExtra("chatName");

        et_message = findViewById(R.id.et_message);
        fab_sendTheMessage = findViewById(R.id.fab_sendTheMessage);
        rv_messages = findViewById(R.id.rv_messages);
        fab_goBack = findViewById(R.id.fab_goBack);
        progressBar = findViewById(R.id.progressBar);

        tv_chatName = findViewById(R.id.tv_chatName);

        tv_chatName.setText(chatName);

        messages = new ArrayList<>();

        adapter = new MessagesAdapter(getApplicationContext(), messages, firebaseUser.getUid());

        rv_messages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_messages.setAdapter(adapter);

        rv_messages.setVisibility(View.GONE);


        fab_sendTheMessage.setEnabled(false);

        Log.d("melech", shtok);
        Log.d("sohn", shtok2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // here we will check weather there are already a chat with each other
        // if they do then it will load the converstaion.
        db.collection("chatRooms").whereEqualTo("studentUid", shtok)
                .whereEqualTo("teacherUid", shtok2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                // Chat room already exists, load messages and show chat UI
                                DocumentSnapshot chatRoomSnapshot = querySnapshot.getDocuments().get(0);
                                ChatModel chatRoom = chatRoomSnapshot.toObject(ChatModel.class);
                                chatRoomId = chatRoomSnapshot.getId();
                                messages = chatRoom.getMessages();
                                // Show chat UI with messages
                                adapter = new MessagesAdapter(getApplicationContext(), messages, firebaseUser.getUid());

                                rv_messages.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);

                                rv_messages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                rv_messages.setAdapter(adapter);
                                rv_messages.scrollToPosition(messages.size() - 1); // Scroll to the last message

                                addChatRoomSnapshotListener();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.d("fuck you", "we didnt find");
                            }
                        }
                    }
                });

        setListeners();
    }

    private void setListeners() {
        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().isEmpty()) {
                    fab_sendTheMessage.setEnabled(true);
                } else {
                    fab_sendTheMessage.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fab_sendTheMessage.setOnClickListener(v -> {
            sendMessage();
        });

        fab_goBack.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * This function will add the time of the message and check if its the first message that has been sent
     * with each other.
     */
    private void sendMessage() {
        String text = et_message.getText().toString().trim();
        et_message.setText("");
        Date date = new Date();

        // Create a SimpleDateFormat object with the "hh:mm" format
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        // Convert the date to a formatted string
        String dateString = formatter.format(date);

        if (text.isEmpty()) return;

        Map<String, Object> message = new HashMap<>();
        message.put("senderUid", firebaseUser.getUid());
        message.put("message", text);
        message.put("messageDate", dateString);

        // checking if the message is new then he would go to function new chat
        if (chatRoomId == null || chatRoomId.isEmpty()) {
            makeANewChatAndSend(message);
        } else {
            sendMessageIntoServer(message);
        }

    }

    /**
     * Sends a message to the server and updates the chat room in Firestore.
     *
     * @param message The message to be sent.
     */
    private void sendMessageIntoServer(Map<String, Object> message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add the message to the messages list
        messages.add(message);

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();

        // Scroll to the last message in the RecyclerView
        rv_messages.scrollToPosition(messages.size() - 1);

        // Update the "messages" field in the chat room document in Firestore
        db.collection("chatRooms").document(chatRoomId)
                .update("messages", messages)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Update the "timestamp" field to the current timestamp
                        db.collection("chatRooms").document(chatRoomId)
                                .update("timestamp", Timestamp.now());

                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Remove the last message from the list if the update fails
                        messages.remove(messages.size() - 1);

                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();

                        // Display an error message to the user
                        Toast.makeText(getApplicationContext(), "Error while trying to send your message! Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    /**
     * Creates a new chat room and sends a message.
     *
     * @param message The message to be sent.
     */
    private void makeANewChatAndSend(Map<String, Object> message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add the message to the messages list
        messages.add(message);
        adapter.notifyDataSetChanged();

        // Create a new ChatModel with the chat room details and messages
        ChatModel chat = new ChatModel(shtok2, shtok, messages);

        // Add the chat room to the "chatRooms" collection in Firestore
        db.collection("chatRooms").add(chat)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Retrieve the ID of the newly created chat room
                            chatRoomId = task.getResult().getId();
                            // Start listening for changes in the chat room
                            addChatRoomSnapshotListener();
                        }
                    }
                });
    }

    /**
     * Adds a snapshot listener to the chat room document in Firestore.
     * Updates the UI with new messages when the document changes.
     */
    private void addChatRoomSnapshotListener() {
        if (chatRoomId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference chatRoomRef = db.collection("chatRooms").document(chatRoomId);

        chatRoomListener = chatRoomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null && value.exists()) {
                    // Chat room document exists, update UI with messages
                    ChatModel chatRoom = value.toObject(ChatModel.class);
                    if (chatRoom != null) {
                        List<Map<String, Object>> updated = chatRoom.getMessages();
                        for (int i = messages.size(); i < updated.size(); i++) {
                            messages.add(updated.get(i));
                        }
                        adapter.notifyDataSetChanged();
                        rv_messages.scrollToPosition(messages.size() - 1); // Scroll to the last message
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the snapshot listener to avoid memory leaks
        if (chatRoomListener != null) {
            chatRoomListener.remove();
        }
    }
}