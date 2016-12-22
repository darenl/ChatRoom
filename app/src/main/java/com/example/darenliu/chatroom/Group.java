package com.example.darenliu.chatroom;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Daren Liu on 7/23/2016.
 */
public class Group implements Serializable {

    private String groupName;
    private String groupId;
    private ArrayList<Lecture> lectures;

    public Group(){

    }

    public Group(String groupName, String groupId){
        this.groupName = groupName;
        this.groupId = groupId;
    }

    public ArrayList<Lecture> getLectures(){ return lectures; }

    public void setLectures(ArrayList<Lecture> lectures){ this.lectures = lectures; }

    public void addLecture(Lecture lecture){ this.lectures.add(lecture); }

    public String getGroupName(){
        return groupName;
    }

    public String getGroupId(){
        return groupId;
    }
}
