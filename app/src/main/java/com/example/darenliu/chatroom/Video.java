package com.example.darenliu.chatroom;

/**
 * Created by Daren Liu on 11/11/2016.
 */

public class Video {

    private Lecture lecture;
    private String title;
    private String url;
    private String id;

    public Video(Lecture lecture, String title, String url, String id){
        this.lecture = lecture;
        this.title = title;
        this.url = url;
        this.id = id;
    }

    public Lecture getLecture() {
        return lecture;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
