package com.example.darenliu.chatroom;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

//Code for Async Tasks learned from tutorials in following link
//http://mobilesiri.com/json-parsing-in-android-using-android-studio/

public class LectureView extends ListActivity {

    // URL to get contacts JSON
    private static String url = "https://shaban.rit.albany.edu/lecture";

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
    private static final String TAG_COURSE_NAME = "name";

    public Course course;
    public User user;
    public String courseId;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra("course");
        user = (User) intent.getSerializableExtra("user");
        courseId = course.getCourseId();
        // Calling async task to get json
        new GetLecture().execute();
    }

    private ArrayList<Lecture> ParseJSONLecture(String json) {
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
                        String url = "https://shaban.rit.albany.edu/" + d.getString(TAG_URL);
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

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static String readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return jsonText;
        } finally {
            is.close();
        }
    }

    private class GetLecture extends AsyncTask<Void, Void, Void> {

        ArrayList<Lecture> lectureList;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(LectureView.this);
            progressDialog.setMessage("Loading all lectures...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            try {
                jsonStr = readJsonFromUrl(url + "?course=" + courseId);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            lectureList = ParseJSONLecture(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            /**
             * Updating received data from JSON into ListView
             */
            final ArrayList<String> listOfLectureNames = new ArrayList<String>();

            for(Lecture lecture: lectureList){
                listOfLectureNames.add(lecture.getLectureDescription());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(LectureView.this, R.layout.list_item, listOfLectureNames);

            ListView listview = (ListView) findViewById(R.id.list);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object item = adapterView.getItemAtPosition(i);
                    int indexOfLecture = listOfLectureNames.indexOf((String) item);
                    Intent intent = new Intent(LectureView.this, LecturePage.class);
                    Lecture lecture = lectureList.get(indexOfLecture);
                    intent.putExtra("lecture", lecture);
                    intent.putExtra("course", course);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });
        }

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.courses_list, menu);
        return true;
    }*/
}
