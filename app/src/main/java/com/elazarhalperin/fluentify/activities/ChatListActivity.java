package com.elazarhalperin.fluentify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.util.RangeValueIterator;
import android.os.Bundle;
import android.widget.Toast;

import com.elazarhalperin.fluentify.MainActivity;
import com.elazarhalperin.fluentify.Models.ChatModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.adapters.ChatsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {
    Query chatsQuery;
    FirebaseUser firebaseUser;
    private ListenerRegistration chatRoomsListener;

    RecyclerView rv_chats;
    ChatsAdapter adapter;
    List<ChatModel> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        String whichUid = HomeActivity.userType.equals("teacher") ? "teacherUid" : "studentUid";
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        chatsQuery = db.collection("chatRooms")
                .whereEqualTo(whichUid, firebaseUser.getUid());

        chats = new ArrayList<>();

        rv_chats = findViewById(R.id.rv_chats);
        rv_chats.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ChatsAdapter(getApplicationContext(), chats, firebaseUser.getUid());
        rv_chats.setAdapter(adapter);


        addChatRoomSnapshotListener();
    }

    private void addChatRoomSnapshotListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        chatRoomsListener = chatsQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    error.printStackTrace();

                    return;
                }
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        ChatModel chatModel = change.getDocument().toObject(ChatModel.class);
                        chatModel.setId(change.getDocument().getId());
                        chats.add(chatModel);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove the snapshot listener to avoid memory leaks
        if (chatRoomsListener != null) {
            chatRoomsListener.remove();
        }
    }

}