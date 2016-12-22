package com.example.darenliu.chatroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

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
public class Course extends Activity {
    Group group;
    User user;
    Lecture lecture;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_room);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        lecture = (Lecture) intent.getSerializableExtra("lecture");

        setTitle(lecture.getLectureDescription());
        TextView groupName = (TextView) findViewById(R.id.groupname);
        TextView lectureDescription = (TextView) findViewById(R.id.lectureDescription);

        groupName.setText("Course: " + group.getGroupName());
        lectureDescription.setText("Description: " + lecture.getLectureDescription());

        File file = new File(getFilesDir(), "video.mp4");

        if (file.exists()) {
            //Code learned from https://android-coffee.com/tutorial-how-to-play-video-in-android-studio-1-4/
            //Code for media controller learned from http://www.techotopia.com/index.php/An_Android_Studio_VideoView_and_MediaController_Tutorial
            VideoView viewMovie = (VideoView) findViewById(R.id.video);
            viewMovie.setVisibility(View.VISIBLE);
            viewMovie.setVideoPath(getFilesDir() + "/video.mp4");
            viewMovie.requestFocus();
            MediaController mediaController = new
                    MediaController(this);
            mediaController.setAnchorView(viewMovie);
            viewMovie.setMediaController(mediaController);
            viewMovie.start();
        }
        else{
            Button downloadButton = (Button) findViewById(R.id.download);
            downloadButton.setVisibility(View.VISIBLE);
            downloadButton.setOnClickListener(new Button.OnClickListener(){

                public void onClick(View view){
                    new DownloadVideos().execute();
                }

            });
        }
    }

    public void startChat(View view){
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("group", group);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    //Code learned from following url
    //http://stackoverflow.com/questions/31718850/download-mp4-from-server-and-save-it-to-sdcard
    private class DownloadVideos extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(Course.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                for(Video video : lecture.getVideo()) {
                    String vidURL = video.getUrl();
                    URL url = new URL(vidURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    String fileName = vidURL.substring(vidURL.lastIndexOf('/'));
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
                }
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

            Intent reloadPage = new Intent();
            reloadPage.putExtra("user", user);
            reloadPage.putExtra("lecture", lecture);

        }

    }
}
