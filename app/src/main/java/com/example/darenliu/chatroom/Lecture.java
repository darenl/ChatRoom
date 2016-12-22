package com.example.darenliu.chatroom;

import java.io.Serializable;
import java.util.ArrayList;

public class Lecture implements Serializable{

    private String lectureDescription;
    private String lectureId;
    private String serialNumber;
    private Group group;
    private ArrayList<Video> videos;
    private String transcript_url;

    public Lecture(String lectureDescription, String lectureId, String serialNumber, String transcript_url){
        this.lectureDescription = lectureDescription;
        this.lectureId = lectureId;
        this.serialNumber = serialNumber;
        this.transcript_url = transcript_url;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public ArrayList<Video> getVideo(){ return videos; }

    public void setVideo(ArrayList<Video> videos){ this.videos = videos; }

    public void addVideo(Video video){ this.videos.add(video); }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getLectureDescription() {
        return lectureDescription;
    }

    public void setLectureDescription(String lectureDescription) {
        this.lectureDescription = lectureDescription;
    }
}
