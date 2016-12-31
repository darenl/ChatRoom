package com.example.darenliu.chatroom;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
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

public class MainActivity extends ListActivity {

    // URL to get contacts JSON
    private static String url = "https://shaban.rit.albany.edu/course";

    //JSON Node variables for course
    private static final String TAG_COURSE_ID = "id";
    private static final String TAG_COURSE_NAME = "name";
    private static final String TAG_USER_NUMBER = "phone";
    private static final String TAG_USER_FIRST_NAME = "firstName";
    private static final String TAG_USER_LAST_NAME = "lastName";

    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_PHONE_STATE"}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        new ConfirmUser().execute();
    }

    private ArrayList<Course> ParseJSONCourse(String json) {
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

    private User ParseJSONUser(String json) {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String phoneNumber = tMgr.getLine1Number();
        if (json != null) {
            try {

                ArrayList<User> userList = new ArrayList<User>();
                JSONArray jsonArray = new JSONArray(json);
                // looping through All Students
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject userObject = jsonArray.getJSONObject(i);
                    String userNumber = userObject.getString(TAG_USER_NUMBER);

                    if(userNumber == phoneNumber) {
                        String firstName = userObject.getString(TAG_USER_FIRST_NAME);
                        String lastName = userObject.getString(TAG_USER_LAST_NAME);
                        return new User(firstName, lastName, userNumber);
                    }
                }

                return new User("James", "Bond", "55555555");
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

    private class GetCourse extends AsyncTask<Void, Void, Void> {

        ArrayList<Course> courseList;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            try {
                jsonStr = readJsonFromUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            courseList = ParseJSONCourse(jsonStr);

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
            final ArrayList<String> listOfCourseNames = new ArrayList<String>();

            for(Course course : courseList){
                listOfCourseNames.add(course.getCourseName());
            }

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, listOfCourseNames);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, listOfCourseNames);

            ListView listview = (ListView) findViewById(R.id.list);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object item = adapterView.getItemAtPosition(i);
                    int indexOfCourse = listOfCourseNames.indexOf((String) item);
                    Intent intent = new Intent(MainActivity.this, LectureView.class);
                    Course course = courseList.get(indexOfCourse);
                    intent.putExtra("course", course);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });
        }

    }

    private class ConfirmUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading all courses...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            try {
                jsonStr = readJsonFromUrl("https://shaban.rit.albany.edu/users");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            user = ParseJSONUser(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            new GetCourse().execute();
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
