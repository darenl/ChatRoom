package com.example.darenliu.chatroom;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Daren Liu on 7/23/2016.
 */
public class Course implements Serializable {

    private String courseName;
    private String courseId;
    private ArrayList<Lecture> lectures;

    public Course(){

    }

    public Course(String courseName, String courseId){
        this.courseName = courseName;
        this.courseId = courseId;
    }

    public ArrayList<Lecture> getLectures(){ return lectures; }

    public void setLectures(ArrayList<Lecture> lectures){ this.lectures = lectures; }

    public void addLecture(Lecture lecture){ this.lectures.add(lecture); }

    public String getCourseName(){
        return courseName;
    }

    public String getCourseId(){
        return courseId;
    }
}
