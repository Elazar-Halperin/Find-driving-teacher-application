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
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.ChatActivity;

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
        if(chats.get(position).getStudentUid().equals(uid)) {
            messageTo = chats.get(position).getTeacherUid();
        } else {
            messageTo = chats.get(position).getStudentUid();
        }
        tv_chatName.setText(messageTo);
        Log.d("Melech", "{ messageTo: " + messageTo +"\nmy uid: " + uid +"}");
        String finalMessageTo = messageTo;
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
