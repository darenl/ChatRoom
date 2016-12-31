package com.example.darenliu.chatroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
public class VideoPage extends Activity {
    Course course;
    User user;
    Lecture lecture;
    Video video;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_room);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        lecture = (Lecture) intent.getSerializableExtra("lecture");
        course = (Course) intent.getSerializableExtra("course");
        video = (Video) intent.getSerializableExtra("video");

        setTitle(lecture.getLectureDescription());
        TextView groupName = (TextView) findViewById(R.id.groupname);
        TextView lectureDescription = (TextView) findViewById(R.id.lectureDescription);
        TextView vidTitle = (TextView) findViewById(R.id.videoTitle);

        groupName.setText("Course: " + course.getCourseName());
        lectureDescription.setText("Description: " + lecture.getLectureDescription());
        vidTitle.setText(video.getTitle());
//        //ArrayList<MediaController> listOfMediaCtrl = new ArrayList<MediaController>();
//        ArrayList<String> listOfVids = new ArrayList<String>();
//        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
//        for (final Video vid : lecture.getVideo()) {
//            TextView titleText = new TextView(LecturePage.this);
//            titleText.setText(vid.getTitle());
//            titleText.setGravity(Gravity.CENTER);
//            layout.addView(titleText);
//            File file = new File(getFilesDir(), vid.getUrl());
//            //File file = new File(getFilesDir(), "vid.mp4");
//            if (file.exists()) {
//                System.out.println("File exists for video " + vid.getUrl());
//                if (!listOfVids.contains(vid.getUrl())) {
//                    listOfVids.add(vid.getUrl());
//                    String vidPath = vid.getUrl();
//                    VideoView watchVid = new VideoView(LecturePage.this);
//                    createVideo(watchVid, vidPath, layout);
//                }
//            } else {
//                System.out.println("File does not exist for video " + vid.getUrl());
//                Button downloadButton = new Button(LecturePage.this);
//                downloadButton.setText("Download " + vid.getTitle());
//                downloadButton.setOnClickListener(new Button.OnClickListener() {
//
//                    public void onClick(View view) {
//                        new DownloadVideos(vid.getUrl()).execute();
//                    }
//
//                });
//                layout.addView(downloadButton);
//            }
//        }

        File file = new File(getFilesDir(), video.getUrl());

        if (file.exists()) {
            //Code learned from https://android-coffee.com/tutorial-how-to-play-video-in-android-studio-1-4/
            //Code for media controller learned from http://www.techotopia.com/index.php/An_Android_Studio_VideoView_and_MediaController_Tutorial
            VideoView viewMovie = (VideoView) findViewById(R.id.video);
            viewMovie.setVisibility(View.VISIBLE);
            viewMovie.setVideoPath(getFilesDir() + "/" + video.getUrl());
            viewMovie.requestFocus();
            MediaController mediaController = new MediaController(this);
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

    //Code learned from https://android-coffee.com/tutorial-how-to-play-video-in-android-studio-1-4/
    //Code for media controller learned from http://www.techotopia.com/index.php/An_Android_Studio_VideoView_and_MediaController_Tutorial
    public void createVideo(VideoView watchVid, String vidPath, LinearLayout layout){
        Display display = getWindowManager().getDefaultDisplay();
        LinearLayout.LayoutParams videoParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        watchVid.setLayoutParams(videoParams);
        watchVid.setVisibility(View.VISIBLE);
        watchVid.setVideoPath(getFilesDir() + "/" + vidPath);
        watchVid.requestFocus();
        layout.addView(watchVid);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(watchVid);
        watchVid.setMediaController(mediaController);
        //watchVid.start();
    }

    public void startChat(View view){
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("course", course);
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
            progressDialog = new ProgressDialog(VideoPage.this);
            progressDialog.setMessage("Downloading Video...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... args) {

            try {
                URL url = new URL(video.getUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                String fileName = video.getUrl();
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

            VideoView viewMovie = (VideoView) findViewById(R.id.video);
            viewMovie.setVisibility(View.VISIBLE);
            viewMovie.setVideoPath(getFilesDir() + "/" + video.getUrl());
            viewMovie.requestFocus();
            MediaController mediaController = new MediaController(VideoPage.this);
            mediaController.setAnchorView(viewMovie);
            viewMovie.setMediaController(mediaController);

        }

    }
}
