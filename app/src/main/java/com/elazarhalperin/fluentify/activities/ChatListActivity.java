package com.elazarhalperin.fluentify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.elazarhalperin.fluentify.Models.ChatModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.UserTypeHelper;
import com.elazarhalperin.fluentify.helpers.adapters.ChatsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    FloatingActionButton fab_goBack;

    Query chatsQuery;
    FirebaseUser firebaseUser;
    private ListenerRegistration chatRoomsListener;

    RecyclerView rv_chats;
    ChatsAdapter adapter;
    List<ChatModel> chats;

    UserTypeHelper userTypeHelper;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userTypeHelper = new UserTypeHelper(getApplicationContext());
        userType = userTypeHelper.getUserType();

        setContentView(R.layout.activity_chat_list);

        String whichUid = userType.equals("teacher") ? "teacherUid" : "studentUid";
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        chatsQuery = db.collection("chatRooms")
                .whereEqualTo(whichUid, firebaseUser.getUid()).orderBy("timestamp", Query.Direction.DESCENDING);

        chats = new ArrayList<>();

        rv_chats = findViewById(R.id.rv_chats);
        rv_chats.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ChatsAdapter(getApplicationContext(), chats, firebaseUser.getUid());
        rv_chats.setAdapter(adapter);


        fab_goBack = findViewById(R.id.fab_goBack);

        fab_goBack.setOnClickListener(v -> {
            finish();
        });
        addChatRoomSnapshotListener();
    }



    /**
     * Adds a snapshot listener to the chat rooms query in Firestore.
     * Listens for changes in the chat rooms and updates the UI accordingly.
     */
    private void addChatRoomSnapshotListener() {
        chatRoomsListener = chatsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                // check if error happend/
                if (error != null) {
                    error.printStackTrace();
                    return;
                }

                for (DocumentChange change : value.getDocumentChanges()) {
                    switch (change.getType()) {
                        case ADDED:
                            // Handle added document
                            ChatModel chatModel = change.getDocument().toObject(ChatModel.class);
                            chatModel.setId(change.getDocument().getId());
                            chats.add(chatModel);
                            adapter.notifyDataSetChanged();
                            break;

                        case MODIFIED:
                            // Handle modified document
                            Toast.makeText(getApplicationContext(), "modified", Toast.LENGTH_SHORT).show();
                            ChatModel modifiedChat = change.getDocument().toObject(ChatModel.class);
                            modifiedChat.setId(change.getDocument().getId());

                            // Find the position of the modified chat in the list
                            int modifiedIndex = findChatIndexById(modifiedChat.getId());

                            if (modifiedIndex != -1) {
                                // Replace the existing chat with the modified chat
                                chats.remove(modifiedIndex);
                                chats.add(0, modifiedChat);
                                adapter.notifyDataSetChanged();
                            }
                            break;

                        case REMOVED:
                            // Handle removed document
                            ChatModel removedChat = change.getDocument().toObject(ChatModel.class);
                            boolean remove = chats.remove(removedChat);
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });
    }

    // Helper method to find the index of a chat in the list based on its ID
    private int findChatIndexById(String chatId) {
        for (int i = 0; i < chats.size(); i++) {
            ChatModel chat = chats.get(i);
            if (chat.getId().equals(chatId)) {
                return i;
            }
        }
        return -1; // Chat not found
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the snapshot listener to avoid memory leaks
        Toast.makeText(getApplicationContext(), "listener removed", Toast.LENGTH_SHORT).show();
        if (chatRoomsListener != null) {
            chatRoomsListener.remove();
        }
    }

}