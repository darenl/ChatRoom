package com.shaban.darenliu.chatroom;

import java.io.Serializable;

/**
 * Created by Daren Liu on 7/24/2016.
 */
public class Message implements Serializable {

    private String username;
    private Course course;
    private String content;
    private int id;


    public Message(String username, Course course, String content){
        this.username = username;
        this.course = course;
        this.content = content;
    }

    public Message(String username, Course course, String content, int id){
        this.username = username;
        this.course = course;
        this.content = content;
        this.id = id;
    }

    public String getUsername(){return username; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
