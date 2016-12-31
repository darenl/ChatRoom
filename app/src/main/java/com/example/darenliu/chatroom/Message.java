package com.example.darenliu.chatroom;

import java.io.Serializable;

/**
 * Created by Daren Liu on 7/24/2016.
 */
public class Message implements Serializable {

    private String username;
    private User user;
    private Course course;
    private String content;
    private String id;

    public Message(User user, Course course, String content){
        this.user = user;
        this.course = course;
        this.content = content;
    }

    public Message(String username, Course course, String content){
        this.username = username;
        this.course = course;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
