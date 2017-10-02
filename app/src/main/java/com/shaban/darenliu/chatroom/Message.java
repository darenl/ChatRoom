package com.shaban.darenliu.chatroom;

import java.io.Serializable;

/**
 * Created by Daren Liu on 7/24/2016.
 */
public class Message implements Serializable {

    private String username;
    private Course course;
    private Lecture lecture;
    private String content;
    private int id;
    private int courseId;


    public Message(String username, String content){
        this.username = username;
        this.content = content;
    }

    public Message(String username, String content, int id){
        this.username = username;
        this.id = id;
        this.content = content;
    }

    public Message(String username, Lecture lecture, String content, int id, int courseId){
        this.username = username;
        this.lecture = lecture;
        this.content = content;
        this.id = id;
        this.courseId = courseId;
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

    public int getCourseId() {
        return courseId;
    }

    public void setLectureId(int courseId) {
        this.courseId = courseId;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }
}
