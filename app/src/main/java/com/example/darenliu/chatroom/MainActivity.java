package com.example.darenliu.chatroom;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
    private static String url = " https://shaban.rit.albany.edu/lecture";

    //JSON Node variables for group
    private static final String TAG_GROUP_ID = "id";
    private static final String TAG_GROUP_NAME = "name";
    private static final String TAG_USER_NUMBER = "phone";
    private static final String TAG_USER_FIRST_NAME = "firstName";
    private static final String TAG_USER_LAST_NAME = "lastName";

    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ConfirmUser().execute();
    }

    private ArrayList<Group> ParseJSONGroup(String json) {
        if (json != null) {
            try {

                ArrayList<Group> groupList = new ArrayList<Group>();
                ArrayList<String> listOfId = new ArrayList<String>();
                JSONArray jsonArray = new JSONArray(json);

                // looping through All Students
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    JSONObject course = (JSONObject) c.get("course");

                    String groupid = course.getString(TAG_GROUP_ID);
                    String groupname = course.getString(TAG_GROUP_NAME);

                    if(!listOfId.contains(groupid)) {
                        groupList.add(new Group(groupname, groupid));
                        listOfId.add(groupid);
                    }
                }
                return groupList;
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
        System.out.println("this is the phone number: " + phoneNumber);
        if (json != null) {
            try {

                ArrayList<User> userList = new ArrayList<User>();
                JSONArray jsonArray = new JSONArray(json);

                // looping through All Students
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    JSONObject course = (JSONObject) c.get("users");
                    String userNumber = course.getString(TAG_USER_NUMBER);

                    if(userNumber == phoneNumber) {
                        String firstName = course.getString(TAG_USER_FIRST_NAME);
                        String lastName = course.getString(TAG_USER_LAST_NAME);
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

    private class GetGroup extends AsyncTask<Void, Void, Void> {

        ArrayList<Group> groupList;
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


            groupList = ParseJSONGroup(jsonStr);

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
            final ArrayList<String> listOfGroupNames = new ArrayList<String>();

            for(Group group: groupList){
                listOfGroupNames.add(group.getGroupName());
            }

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, listOfGroupNames);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, listOfGroupNames);

            ListView listview = (ListView) findViewById(R.id.list);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object item = adapterView.getItemAtPosition(i);
                    int indexOfGroup = listOfGroupNames.indexOf((String) item);
                    Intent intent = new Intent(MainActivity.this, LectureView.class);
                    Group group = groupList.get(indexOfGroup);
                    intent.putExtra("group", group);
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
            progressDialog.setMessage("Please wait...");
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
            new GetGroup().execute();
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
