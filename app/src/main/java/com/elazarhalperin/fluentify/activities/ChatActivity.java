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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.elazarhalperin.fluentify.Models.ChatModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.adapters.MessagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
    EditText et_message;
    FloatingActionButton fab_sendTheMessage;

    RecyclerView rv_messages;
    MessagesAdapter adapter;
    List<Map<String, Object>> messages;
    String shtok;
    String shtok2;
    String userType;

    String chatRoomId;
    String messageTo;
    private ListenerRegistration chatRoomListener;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatRoomId = getIntent().getStringExtra("chatRoomId");

        et_message = findViewById(R.id.et_message);
        fab_sendTheMessage = findViewById(R.id.fab_sendTheMessage);
        rv_messages = findViewById(R.id.rv_messages);

        messages = new ArrayList<>();

        adapter = new MessagesAdapter(getApplicationContext(), messages, firebaseUser.getUid());

        rv_messages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_messages.setAdapter(adapter);

        userType = HomeActivity.userType;
        messageTo = getIntent().getStringExtra("messageTo");
        shtok = HomeActivity.userType.equals("student") ? firebaseUser.getUid() : messageTo;
        shtok2 = HomeActivity.userType.equals("teacher") ? firebaseUser.getUid() : messageTo;

        Log.d("melech", shtok);
        Log.d("sohn", shtok2);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chatRooms").whereEqualTo("studentUid", userType.equals("student") ? shtok2 : shtok)
                .whereEqualTo("teacherUid", userType.equals("teacher") ? shtok2 : shtok)
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

                                rv_messages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                rv_messages.setAdapter(adapter);
                                rv_messages.scrollToPosition(messages.size() - 1); // Scroll to the last message

                            } else {
                                Log.d("fuck you", "we didnt find");
                            }
                        }
                    }
                });
        addChatRoomSnapshotListener();
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
    }

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

    private void sendMessageIntoServer(Map<String, Object> message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        messages.add(message);
        adapter.notifyDataSetChanged();
        rv_messages.scrollToPosition(messages.size() - 1); // Scroll to the last message

        db.collection("chatRooms").document(chatRoomId)
                .update("messages", messages)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        messages.remove(messages.size() - 1);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Error while trying sending your message! Pls try again.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void makeANewChatAndSend(Map<String, Object> message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        messages.add(message);
        adapter.notifyDataSetChanged();

        ChatModel chat = new ChatModel(shtok, shtok2, messages);

        db.collection("chatRooms").add(chat)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            chatRoomId = task.getResult().getId();
                            addChatRoomSnapshotListener();
                        }
                    }
                });

    }

    private void addChatRoomSnapshotListener() {
        if (chatRoomId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference chatRoomRef = db.collection("chatRooms").document(chatRoomId);

        chatRoomListener = chatRoomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    return;
                }

                if (value != null && value.exists()) {
                    // Chat room document exists, update UI with messages
                    ChatModel chatRoom = value.toObject(ChatModel.class);
                    if (chatRoom != null) {
                        List<Map<String, Object>> updated = chatRoom.getMessages();
                        for(int i = messages.size(); i < updated.size(); i++) {
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