package com.example.darenliu.chatroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Author: Naoyuki Kanezawa
 * Date: 2016
 * Availability: https://github.com/nkzawa/socket.io-android-chat/blob/master/app/src/main/java/com/github/nkzawa/socketio/androidchat/MessageAdapter.java
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private int[] usernameColors;
    private String username;

    public MessageAdapter(Context context, List<Message> messages, String username) {
        this.username = username;
        this.messages = messages;
        usernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        layout = R.layout.item_message;
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v, username);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = messages.get(position);
        viewHolder.setMessage(message.getContent());
        viewHolder.setUsername(message.getUsername());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameView;
        private TextView messageView;
        private String username;

        public ViewHolder(View itemView, String username) {
            super(itemView);
            this.username = username;
            usernameView = (TextView) itemView.findViewById(R.id.username);
            messageView = (TextView) itemView.findViewById(R.id.message);
        }

        public void setUsername(String username) {
            if (null == usernameView) return;
            usernameView.setText(username);
            usernameView.setTextColor(getUsernameColor(username));
        }

        public void setMessage(String message) {
            if (null == messageView) return;
            messageView.setText(message);
        }

        private int getUsernameColor(String username) {
            int index;
            if(this.username.equals(username))
                index = 0;
            else
                index = 1;
            return usernameColors[index];
        }
    }
}

