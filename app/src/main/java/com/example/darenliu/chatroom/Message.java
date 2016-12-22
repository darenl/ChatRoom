package com.example.darenliu.chatroom;

import java.io.Serializable;

/**
 * Created by Daren Liu on 7/24/2016.
 */
public class Message implements Serializable {

    private String username;
    private User user;
    private Group group;
    private String content;
    private String id;

    public Message(User user, Group group, String content){
        this.user = user;
        this.group = group;
        this.content = content;
    }

    public Message(String username, Group group, String content){
        this.username = username;
        this.group = group;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
