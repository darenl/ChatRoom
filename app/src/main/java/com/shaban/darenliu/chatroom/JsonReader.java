package com.shaban.darenliu.chatroom;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by dliu7 on 1/9/2017.
 */
/***********************************************************************************
 * Author: Muhammad Bilal
 * Date: 9/28/16
 * Availability: http://mobilesiri.com/json-parsing-in-android-using-android-studio/
 * Notes: Code for parsing json file asynchronously
 ***********************************************************************************/
public class JsonReader {

    //JSON Node variables for course
    private static final String TAG_COURSE_ID = "id";
    private static final String TAG_COURSE_NAME = "name";
    private static final String TAG_USER_NUMBER = "phone";
    private static final String TAG_USER_FIRST_NAME = "firstName";
    private static final String TAG_USER_LAST_NAME = "lastName";

    //JSON Node variables for lectures
    private static final String TAG_LECTURE = "lecture";
    private static final String TAG_LECTURE_ID = "id";
    private static final String TAG_LECTURE_NAME = "description";
    private static final String TAG_SERIAL_NUMBER = "serial_number";
    private static final String TAG_TRANSCRIPT_URL = "transcript_url";
    private static final String TAG_VIDEOS = "videos";
    private static final String TAG_TITLE = "title";
    private static final String TAG_URL = "url";
    private static final String TAG_COURSES = "course";

    public static ArrayList<Lecture> ParseJSONLecture(String json) {
        if (json != null) {
            try {

                ArrayList<Lecture> lectureList = new ArrayList<Lecture>();
                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);

                    String lectureid = c.getString(TAG_LECTURE_ID);
                    String lecturename = c.getString(TAG_LECTURE_NAME);
                    String serialnumber = c.getString(TAG_SERIAL_NUMBER);
                    String transcript_url = c.getString(TAG_TRANSCRIPT_URL);
                    Lecture lecture = new Lecture(lecturename, lectureid, serialnumber, transcript_url);
                    JSONArray videosArray = c.getJSONArray(TAG_VIDEOS);
                    ArrayList<Video> videoList = new ArrayList<Video>();
                    ArrayList<Course> courseList = new ArrayList<Course>();
                    for(int j = 0; j < videosArray.length(); j++){
                        JSONObject d = videosArray.getJSONObject(j);
                        String id = d.getString(TAG_LECTURE);
                        String lecture_id = d.getString(TAG_LECTURE_ID);
                        String title = d.getString(TAG_TITLE);
                        String url = "https://shaban.rit.albany.edu" + d.getString(TAG_URL);
                        videoList.add(new Video(lecture, title, url, id));
                    }
                    lecture.setVideo(videoList);

                    JSONObject e = c.getJSONObject("course");
                    String course_name = e.getString(TAG_COURSE_NAME);
                    String course_id = e.getString(TAG_LECTURE_ID);
                    lecture.setCourse(new Course(course_name, course_id));
                    lectureList.add(lecture);
                }
                return lectureList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    public static ArrayList<Course> ParseJSONCourse(String json) {
        if (json != null) {
            try {

                ArrayList<Course> courseList = new ArrayList<Course>();
                ArrayList<String> listOfId = new ArrayList<String>();
                JSONArray jsonArray = new JSONArray(json);

                // looping through All Students
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject course = jsonArray.getJSONObject(i);

                    String courseid = course.getString(TAG_COURSE_ID);
                    String coursename = course.getString(TAG_COURSE_NAME);

                    if(!listOfId.contains(courseid)) {
                        courseList.add(new Course(coursename, courseid));
                        listOfId.add(courseid);
                    }
                }
                return courseList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    public static User ParseJSONUser(String json) {
        if (json != null) {
            try {
                JSONObject userObject = new JSONObject(json);
                String userNumber = userObject.getString(TAG_USER_NUMBER);
                String firstName = userObject.getString(TAG_USER_FIRST_NAME);
                String lastName = userObject.getString(TAG_USER_LAST_NAME);
                return new User(firstName, lastName, userNumber);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    public static ArrayList<Message> ParseJSONMessage(String json, Course course) {
        if (json != null) {
            try {

                JSONArray jsonArray = new JSONArray(json);
                ArrayList<Message> messageList = new ArrayList<Message>();

                // looping through All Messages
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject message = jsonArray.getJSONObject(i);
                    JSONObject author = message.getJSONObject("author");
                    JSONObject group = message.getJSONObject("group");
                    String name = author.getString(TAG_USER_FIRST_NAME) + " " + author.getString(TAG_USER_LAST_NAME);
                    String courseId = group.getString("course");
                    String content = message.getString("content");
                    int id = Integer.parseInt(message.getString("id"));
                    if(courseId.equals(course.getCourseId()))
                        messageList.add(new Message(name, new Course(courseId), content, id));

                }
                return messageList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }



    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return jsonText;
        } finally {
            is.close();
        }
    }
}
