package com.example.darenliu.chatroom;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.webkit.WebView;
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
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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

        Button downloadButton = (Button) findViewById(R.id.download);
        downloadButton.setVisibility(View.VISIBLE);
        downloadButton.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View view){
                new DownloadTranscript().execute();
            }

        });
    }

    /*
     * Author: Tamás Cseh
     * Date: 2015
     * Availability: http://stackoverflow.com/questions/31718850/download-mp4-from-server-and-save-it-to-sdcard
     */
    private class DownloadTranscript extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        File file;

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
                int permission = ActivityCompat.checkSelfPermission(TranscriptPage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TranscriptPage.this, PERMISSIONS_STORAGE, 1);
                }

                URL url = new URL("https://shaban.rit.albany.edu" + transcript);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                String fileName = transcript.split("/")[2];
                file = new File(getApplicationContext().getFilesDir().toString(), fileName);

                if (!file.exists())
                    file.createNewFile();


                FileOutputStream outputStream = new FileOutputStream(file);
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

            /**
             * Author: Naveed Ahmad
             * Date: 7/2/16
             * Availability: http://stackoverflow.com/questions/38159187/opening-pdf-file-error-this-file-could-not-be-accessed-check-the-location-or-th
             */
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String filePath = file.getAbsolutePath();
            Uri uriFile = Uri.parse("content://com.example.darenliu.chatroom/" + filePath);
            intent.setDataAndType(uriFile, "application/pdf");
            Intent intentPDF = Intent.createChooser(intent, "Choose Pdf Application");
            startActivity(intentPDF);

        }

    }
}
