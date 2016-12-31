package com.example.darenliu.chatroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Daren Liu on 7/25/2016.
 */
public class TranscriptPage extends Activity {
    Course course;
    User user;
    Lecture lecture;
    String transcript;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transcript_room);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        lecture = (Lecture) intent.getSerializableExtra("lecture");
        course = (Course) intent.getSerializableExtra("course");
        transcript = (String) intent.getSerializableExtra("transcript");

        setTitle(lecture.getLectureDescription());
        TextView groupName = (TextView) findViewById(R.id.groupname);
        TextView lectureDescription = (TextView) findViewById(R.id.lectureDescription);

        groupName.setText("Course: " + course.getCourseName());
        lectureDescription.setText("Description: " + lecture.getLectureDescription());

        File file = new File(getFilesDir(), transcript.split("/")[2]);

        if (file.exists()) {
            System.out.println("Hello the file somehow exists");

        }
        else{
            System.out.println("it dont exist");
            Button downloadButton = (Button) findViewById(R.id.download);
            downloadButton.setVisibility(View.VISIBLE);
            downloadButton.setOnClickListener(new Button.OnClickListener(){

                public void onClick(View view){
                    new DownloadTranscript().execute();
                }

            });
        }
    }

    //Code learned from following url
    //http://stackoverflow.com/questions/31718850/download-mp4-from-server-and-save-it-to-sdcard
    private class DownloadTranscript extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(TranscriptPage.this);
            progressDialog.setMessage("Downloading PDF...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... args) {

            try {
                System.out.println("https://shaban.rit.albany.edu" + transcript);
                URL url = new URL("https://shaban.rit.albany.edu" + transcript);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                String fileName = transcript.split("/")[2];
                System.out.println("Downloading " + fileName);
                File output = new File(getFilesDir(), fileName);

                if (!output.exists())
                    output.createNewFile();


                FileOutputStream outputStream = new FileOutputStream(output);
                InputStream inputStream = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.close();
                inputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();


        }

    }
}
