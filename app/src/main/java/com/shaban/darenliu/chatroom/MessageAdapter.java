package com.shaban.darenliu.chatroom;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = messages.get(position);
        viewHolder.setUsernameAndMessage(message.getUsername(), message.getContent());
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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void setUsernameAndMessage(String username, String message) {
            if (null == usernameView) return;
            if (null == messageView) return;

            RelativeLayout.LayoutParams usernameLayout = (RelativeLayout.LayoutParams) usernameView.getLayoutParams();
            RelativeLayout.LayoutParams messageLayout = (RelativeLayout.LayoutParams) messageView.getLayoutParams();

            if(this.username.equals(username)) {
                usernameLayout.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                messageLayout.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                usernameLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                messageLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
            else{
                usernameLayout.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                messageLayout.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                usernameLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                messageLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }

            usernameView.setText(username);
            usernameView.setTextColor(getUsernameColor(username));

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

