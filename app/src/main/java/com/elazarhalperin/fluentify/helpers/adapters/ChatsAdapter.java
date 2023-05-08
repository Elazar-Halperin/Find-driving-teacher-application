package com.elazarhalperin.fluentify.helpers.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elazarhalperin.fluentify.Models.ChatModel;
import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.Models.UserModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.ChatActivity;
import com.elazarhalperin.fluentify.helpers.UserTypeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.Holder> {
    Context context;
    List<ChatModel> chats;
    String uid;

    public ChatsAdapter(Context context, List<ChatModel> chats, String uid) {
        this.context = context;
        this.chats = chats;
        this.uid = uid;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final TextView tv_chatName = holder.getTv_chatName();
        final ImageView iv_chatProfile = holder.getIv_chatProfile();
        final LinearLayout ll_container = holder.getLl_container();
        String messageTo = "";
        boolean isUserStudent = chats.get(position).getStudentUid().equals(uid);
        if(isUserStudent) {
            messageTo = chats.get(position).getTeacherUid();
        } else {
            messageTo = chats.get(position).getStudentUid();
        }

        Log.d("Melech", "{ messageTo: " + messageTo +"\nmy uid: " + uid +"}");
        String finalMessageTo = messageTo;

        DocumentReference ref = FirebaseFirestore.getInstance().collection(isUserStudent ? "teachers" : "students").document(messageTo);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    UserModel user = task.getResult().toObject(UserModel.class);
                    tv_chatName.setText(user.getName());
                } else {

                }
            }
        });


        ll_container.setOnClickListener(v-> {
            Intent i = new Intent(context, ChatActivity.class);
            i.putExtra("chatRoomId", chats.get(position).getId());
            i.putExtra("messageTo", finalMessageTo);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line to set the flag
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView tv_chatName;
        final ImageView iv_chatProfile;
        final LinearLayout ll_container;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tv_chatName = itemView.findViewById(R.id.tv_chatName);
            iv_chatProfile = itemView.findViewById(R.id.iv_chatProfile);
            ll_container = itemView.findViewById(R.id.ll_container);
        }

        public LinearLayout getLl_container() {
            return ll_container;
        }

        public ImageView getIv_chatProfile() {
            return iv_chatProfile;
        }

        public TextView getTv_chatName() {
            return tv_chatName;
        }
    }
}
