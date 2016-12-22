package com.example.darenliu.chatroom;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;


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
