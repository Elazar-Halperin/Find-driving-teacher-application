package com.elazarhalperin.fluentify.helpers.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elazarhalperin.fluentify.R;

import java.util.List;
import java.util.Map;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.Holder> {

    Context context;
    List<Map<String, Object>> messages;
    String uid;

    public MessagesAdapter(Context context, List<Map<String, Object>> messages, String uid) {
        this.context = context;
        this.messages = messages;
        this.uid = uid;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final TextView tv_message = holder.getTv_message();
        final TextView tv_messageDate = holder.getTv_messageDate();
        final LinearLayout ll_container = holder.getLl_container();

        if (messages.get(position).get("senderUid").toString().equals(uid)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.START;
            ll_container.setLayoutParams(params);
            ll_container.setBackground(context.getDrawable(R.drawable.message_background));
            tv_messageDate.setGravity(Gravity.START);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.END;
            ll_container.setLayoutParams(params);
            tv_messageDate.setGravity(Gravity.END);
            ll_container.setBackground(context.getDrawable(R.drawable.message_background2));
        }

        tv_message.setText(messages.get(position).get("message").toString());
        // Set default visibility to GONE for date view
        tv_messageDate.setVisibility(View.GONE);

        if (position == messages.size() - 1) {
            // Display the date for the last message in the list
            tv_messageDate.setText(messages.get(position).get("messageDate").toString());
            tv_messageDate.setVisibility(View.VISIBLE); // Set visibility to VISIBLE for last message date
        } else {
            // Check if the current message and the next message have the same sender UID
            if (!messages.get(position).get("senderUid").equals(messages.get(position + 1).get("senderUid"))) {
                // Show the date view for all other messages with different sender UIDs
                tv_messageDate.setVisibility(View.VISIBLE);
                tv_messageDate.setText(messages.get(position).get("messageDate").toString());
            }
        }
        Log.d("messageSize", messages.size() + "");
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        final TextView tv_message, tv_messageDate;
        final LinearLayout ll_container, ll_main;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tv_message = itemView.findViewById(R.id.tv_message);
            tv_messageDate = itemView.findViewById(R.id.tv_messageDate);
            ll_container = itemView.findViewById(R.id.ll_container);
            ll_main = itemView.findViewById(R.id.ll_main);
        }

        public LinearLayout getLl_container() {
            return ll_container;
        }

        public TextView getTv_message() {
            return tv_message;
        }

        public TextView getTv_messageDate() {
            return tv_messageDate;
        }

        public LinearLayout getLl_main() {
            return ll_main;
        }
    }
}
