package com.shaban.darenliu.chatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dliu7 on 1/6/2017.
 */

public class LectureFragment extends android.support.v4.app.ListFragment{

    // URL to get contacts JSON
    private static String url = "https://shaban.rit.albany.edu/lecture";

    public Course course;
    public User user;
    public String courseId;
    ArrayList<Lecture> lectureList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        course = (Course) intent.getSerializableExtra("course");
        user = (User) intent.getSerializableExtra("user");
        courseId = course.getCourseId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Calling async task to get json
        new GetLecture().execute();
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        super.onCreateOptionsMenu(menu, R.menu.courses_list);getMenuInflater().inflate(R.menu.courses_list, menu);
//        return true;
//    }

    /***********************************************************************************
     * Author: Muhammad Bilal
     * Date: 9/28/16
     * Availability: http://mobilesiri.com/json-parsing-in-android-using-android-studio/
     * Notes: Code for parsing json file asynchronously
     ***********************************************************************************/
    private class GetLecture extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading all lectures...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            if(lectureList == null) {
                try {
                    jsonStr = JsonReader.readJsonFromUrl(url + "?course=" + courseId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                lectureList = JsonReader.ParseJSONLecture(jsonStr);
            }
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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, listOfLectureNames);

            ListView listview = (ListView) getView().findViewById(android.R.id.list);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object item = adapterView.getItemAtPosition(i);
                    int indexOfLecture = listOfLectureNames.indexOf((String) item);
                    Intent intent = new Intent(getActivity(), LecturePage.class);
                    Lecture lecture = lectureList.get(indexOfLecture);
                    intent.putExtra("lecture", lecture);
                    intent.putExtra("course", course);
                    intent.putExtra("user", user);
                    getActivity().startActivity(intent);
                }
            });
        }

    }

}
