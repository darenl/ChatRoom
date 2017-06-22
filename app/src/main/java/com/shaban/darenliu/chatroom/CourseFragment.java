package com.shaban.darenliu.chatroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dliu7 on 1/6/2017.
 */

public class CourseFragment extends ListFragment {

    // URL to get contacts JSON
    private static String url = "https://shaban.rit.albany.edu/course";

    public User user;
    ArrayList<Course> courseList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(user == null)
            new ConfirmUser().execute();
        else
            new GetCourse().execute();
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        super.onViewCreated(view, savedInstanceState);
    }

    /***********************************************************************************
     * Author: Muhammad Bilal
     * Date: 9/28/16
     * Availability: http://mobilesiri.com/json-parsing-in-android-using-android-studio/
     * Notes: Code for parsing json file asynchronously
     ***********************************************************************************/
    private class GetCourse extends AsyncTask<Void, Void, Void> {


        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            if(courseList == null) {
                try {
                    jsonStr = JsonReader.readJsonFromUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                courseList = JsonReader.ParseJSONCourse(jsonStr);
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
            final ArrayList<String> listOfCourseNames = new ArrayList<String>();

            for(Course course : courseList){
                listOfCourseNames.add(course.getCourseName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, listOfCourseNames);

            ListView listview = (ListView) getView().findViewById(android.R.id.list);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object item = adapterView.getItemAtPosition(i);
                    int indexOfCourse = listOfCourseNames.indexOf((String) item);
                    Intent intent = new Intent(getActivity().getBaseContext(), LectureView.class);
                    Course course = courseList.get(indexOfCourse);
                    intent.putExtra("course", course);
                    intent.putExtra("user", user);
                    CourseFragment.this.getActivity().startActivity(intent);
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
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            if(ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
            }

            if(ContextCompat.checkSelfPermission(getActivity().getBaseContext(), "android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_PHONE_STATE"}, REQUEST_CODE_ASK_PERMISSIONS);
            }
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading all courses...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

            String phoneNumber = tMgr.getLine1Number();
            try {
                jsonStr = JsonReader.readJsonFromUrl("https://shaban.rit.albany.edu/users/" + phoneNumber);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            user = JsonReader.ParseJSONUser(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if(user != null)
                new GetCourse().execute();
            else {
                Toast toast = new Toast(getContext());
                toast.makeText(getContext(), "Error: Not a registered user", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
