package com.example.darenliu.chatroom;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * A chat fragment containing messages view and input form.
 * Using library from following github link
 * Author: Naoyuki Kanezawa
 * Date: 2016
 * Availability: https://github.com/nkzawa/socket.io-android-chat/blob/master/app/src/main/java/com/github/nkzawa/socketio/androidchat/ChatApplication.java
 */
public class ChatApplication extends Application {

    public Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://shaban.rit.albany.edu");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
