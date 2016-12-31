package com.example.darenliu.chatroom;

/**
 * Created by Daren Liu on 7/7/2016.
 */
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WebRequest {
    // URL to get contacts JSON
    private static String url = "http://courseapp.n2iw.com";

    // JSON Node variables for users
    private static final String TAG_USER_ID = "id";
    private static final String TAG_UNIVERSITY_ID = "university_id";
    private static final String TAG_USER_NAME = "name";
    private static final String TAG_ARABIC_NAME = "arabic_name";
    private static final String TAG_WHATSAPP = "whatsapp";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_LOCATION = "location";

    //JSON Node variables to server
    private static final String TAG_TO_SERVER = "Send to server";
    private static final String TAG_AUTHOR_ID = "author_id";
    private static final String TAG_SERVER_GROUP_ID = "group_id";
    private static final String TAG_CONTENT = "content";

    //JSON Node variables from server
    private static final String TAG_FROM_SERVER = "From server";
    private static final String TAG_AUTHOR_NAME = "author";
    private static final String TAG_GROUP_NAME_FROM_SERVER = "course";
    private static final String TAG_CONTENT_FROM_SERVER = "content";
    private static final String TAG_AUTHOR_ID_FROM_SERVER = "author_id";
    private static final String TAG_GROUP_ID_FROM_SERVER = "group_id";

    private ArrayList<HashMap<String, String>> ParseJSONUser(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();
                JSONArray jsonArray = new JSONArray(json);
                /*jsonObj.put("id", "1");
                jsonObj.put("university_id", "1");
                jsonObj.put("arabic_name", "Arabic Name");
                jsonObj.put("name", "Israa Morganah");
                jsonObj.put("whatsapp", "945807367");
                jsonObj.put("email", "null");
                jsonObj.put("location", "South");*/
                // Getting JSON Array node
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String userid = obj.getString(TAG_USER_ID);
                    String universityid = obj.getString(TAG_UNIVERSITY_ID);
                    String username = obj.getString(TAG_USER_NAME);
                    String arabicname = obj.getString(TAG_ARABIC_NAME);
                    String whatsapp = obj.getString(TAG_WHATSAPP);
                    String email = obj.getString(TAG_EMAIL);
                    String location = obj.getString(TAG_LOCATION);

                    // tmp hashmap for single user
                    HashMap<String, String> user = new HashMap<String, String>();

                    // adding every child node to HashMap key => value
                    user.put(TAG_USER_ID, userid);
                    user.put(TAG_UNIVERSITY_ID, universityid);
                    user.put(TAG_USER_NAME, username);
                    user.put(TAG_ARABIC_NAME, arabicname);
                    user.put(TAG_WHATSAPP, whatsapp);
                    user.put(TAG_EMAIL, email);
                    user.put(TAG_LOCATION, location);

                    // adding user to users list
                    userList.add(user);
                }
                return userList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    private ArrayList<HashMap<String, String>> ParseJSONMessageToServer(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> messageToServerList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);

                // Getting JSON Array node
                JSONArray messagesToServer = jsonObj.getJSONArray(TAG_TO_SERVER);

                // looping through All Students
                for (int i = 0; i < messagesToServer.length(); i++) {
                    JSONObject c = messagesToServer.getJSONObject(i);

                    String authorid = c.getString(TAG_AUTHOR_ID);
                    String groupid = c.getString(TAG_SERVER_GROUP_ID);
                    String content = c.getString(TAG_CONTENT);

                    // tmp hashmap for single user
                    HashMap<String, String> message = new HashMap<String, String>();

                    // adding every child node to HashMap key => value
                    message.put(TAG_AUTHOR_ID, authorid);
                    message.put(TAG_SERVER_GROUP_ID, groupid);
                    message.put(TAG_CONTENT, content);

                    // adding user to users list
                    messageToServerList.add(message);
                }

                return messageToServerList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }
/*
    private ArrayList<HashMap<String, String>> ParseJSONMessageFromServer(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> messageFromServerList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);

                // Getting JSON Array node
                JSONArray messagesFromServer = jsonObj.getJSONArray(TAG_FROM_SERVER);

                for (int i = 0; i < messagesFromServer.length(); i++) {
                    JSONObject c = messagesFromServer.getJSONObject(i);

                    String author = c.getString(TAG_AUTHOR_ID_FROM_SERVER);
                    String course = c.getString(TAG_GROUP_NAME_FROM_SERVER);
                    String content = c.getString(TAG_CONTENT_FROM_SERVER);
                    String authorid = c.getString(TAG_AUTHOR_ID_FROM_SERVER);
                    String groupid = c.getString(TAG_GROUP_ID_FROM_SERVER);

                    // tmp hashmap for single user
                    HashMap<String, String> message = new HashMap<String, String>();

                    message.put(TAG_AUTHOR_ID_FROM_SERVER, author);
                    message.put(TAG_GROUP_NAME_FROM_SERVER, course);
                    message.put(TAG_CONTENT, content);
                    message.put(TAG_AUTHOR_ID_FROM_SERVER, authorid);
                    message.put(TAG_GROUP_ID_FROM_SERVER, groupid);

                    // adding user to users list
                    messageFromServerList.add(message);
                }


                return messageFromServerList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    private class GetUsers extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> userList;
        ProgressDialog proDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            proDialog = new ProgressDialog(MainActivity.this);
            proDialog.setMessage("Please wait...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();
            url += "/users";
            // Making a request to url and getting response
            String jsonStr = null;
            try {
                jsonStr = readJsonFromUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Response: ", "> " + jsonStr);
            userList = ParseJSONUser(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            // Dismiss the progress dialog
            if (proDialog.isShowing())
                proDialog.dismiss();
            /**
             TextView id = (TextView) findViewById(R.id.id);
             TextView university_id = (TextView) findViewById(R.id.university_id);
             TextView arabic_name = (TextView) findViewById(R.id.arabic_name);
             TextView name = (TextView) findViewById(R.id.name);
             TextView whatsapp = (TextView) findViewById(R.id.whatsapp);
             TextView email = (TextView) findViewById(R.id.email);
             TextView location = (TextView) findViewById(R.id.location);

             id.setText(user.get(TAG_USER_ID));
             university_id.setText(user.get(TAG_UNIVERSITY_ID));
             arabic_name.setText(user.get(TAG_ARABIC_NAME));
             name.setText(user.get(TAG_USER_NAME));
             whatsapp.setText(user.get(TAG_WHATSAPP));
             email.setText(user.get(TAG_EMAIL));
             location.setText(user.get(TAG_LOCATION));**/
    /**
     * Updating received data from JSON into ListView
     *
     ListAdapter adapter = new SimpleAdapter(
     MainActivity.this, userList,
     R.layout.list_item, new String[]{TAG_USER_ID, TAG_UNIVERSITY_ID, TAG_ARABIC_NAME, TAG_USER_NAME, TAG_WHATSAPP, TAG_EMAIL, TAG_LOCATION},
     new int[]{R.id.id, R.id.university_id, R.id.arabic_name, R.id.name, R.id.whatsapp, R.id.email, R.id.location});

     setListAdapter(adapter);
     }

     }

     private class GetMessagesToServer extends AsyncTask<Void, Void, Void> {

     // Hashmap for ListView
     ArrayList<HashMap<String, String>> userList;
     ProgressDialog proDialog;

     @Override
     protected void onPreExecute() {
     super.onPreExecute();
     // Showing progress loading dialog
     proDialog = new ProgressDialog(MainActivity.this);
     proDialog.setMessage("Please wait...");
     proDialog.setCancelable(false);
     proDialog.show();
     }

     @Override
     protected Void doInBackground(Void... arg0) {
     // Creating service handler class instance
     WebRequest webreq = new WebRequest();
     url += "/messages";
     // Making a request to url and getting response
     String jsonStr = webreq.makeWebServiceCall(url, WebRequest.GETRequest);

     Log.d("Response: ", "> " + jsonStr);

     userList = ParseJSONMessageToServer(jsonStr);

     return null;
     }

     @Override
     protected void onPostExecute(Void requestresult) {
     super.onPostExecute(requestresult);
     // Dismiss the progress dialog
     if (proDialog.isShowing())
     proDialog.dismiss();
     /**
      * Updating received data from JSON into ListView
      *
     ListAdapter adapter = new SimpleAdapter(
     MainActivity.this, userList,
     R.layout.list_item, new String[]{TAG_USER_NAME, TAG_EMAIL},
     new int[]{R.id.name, R.id.email});

     //setListAdapter(adapter);
     }

     }

     private class GetMessagesFromServer extends AsyncTask<Void, Void, Void> {

     // Hashmap for ListView
     ArrayList<HashMap<String, String>> userList;
     ProgressDialog proDialog;

     @Override
     protected void onPreExecute() {
     super.onPreExecute();
     // Showing progress loading dialog
     proDialog = new ProgressDialog(MainActivity.this);
     proDialog.setMessage("Please wait...");
     proDialog.setCancelable(false);
     proDialog.show();
     }

     @Override
     protected Void doInBackground(Void... arg0) {
     // Creating service handler class instance
     WebRequest webreq = new WebRequest();
     url += "/messages";
     // Making a request to url and getting response
     String jsonStr = webreq.makeWebServiceCall(url, WebRequest.GETRequest);

     Log.d("Response: ", "> " + jsonStr);

     userList = ParseJSONMessageFromServer(jsonStr);

     return null;
     }

     @Override
     protected void onPostExecute(Void requestresult) {
     super.onPostExecute(requestresult);
     // Dismiss the progress dialog
     if (proDialog.isShowing())
     proDialog.dismiss();
     /**
      * Updating received data from JSON into ListView
      *
     ListAdapter adapter = new SimpleAdapter(
     MainActivity.this, userList,
     R.layout.list_item, new String[]{TAG_USER_NAME, TAG_EMAIL},
     new int[]{R.id.name, R.id.email});

     //setListAdapter(adapter);
     }

     }*/
}