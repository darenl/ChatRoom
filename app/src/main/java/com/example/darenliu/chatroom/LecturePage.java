package com.example.darenliu.chatroom;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;


/**
 * Created by Daren Liu on 7/25/2016.
 */
public class LecturePage extends Activity {
    Course course;
    User user;
    Lecture lecture;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecture_room);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        lecture = (Lecture) intent.getSerializableExtra("lecture");
        course = (Course) intent.getSerializableExtra("course");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LecturePage.this, R.layout.list_item, lecture.getVideoTitles());
        ArrayAdapter<String> adapterTranscript = new ArrayAdapter<String>(LecturePage.this, R.layout.list_item, lecture.getShortenedUrl());

        ListView listview = (ListView) findViewById(R.id.list1);
        ListView listviewTranscript = (ListView) findViewById(R.id.list2);
        listview.setAdapter(adapter);
        listviewTranscript.setAdapter(adapterTranscript);
        setHeight(listview);
        setHeight(listviewTranscript);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                int indexOfVideo = lecture.getVideoTitles().indexOf((String) item);
                Intent intent = new Intent(LecturePage.this, VideoPage.class);
                Video video = lecture.getVideo().get(indexOfVideo);
                intent.putExtra("lecture", lecture);
                intent.putExtra("course", course);
                intent.putExtra("video", video);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        listviewTranscript.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);

                int indexOfTranscript = lecture.getTranscriptUrl().indexOf("/transcripts/" + (String) item);
                String transcript = lecture.getTranscriptUrl().get(indexOfTranscript);
                int permission = ActivityCompat.checkSelfPermission(LecturePage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LecturePage.this, PERMISSIONS_STORAGE, 1);
                }
                //String uri = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + transcript.split("/")[2];
                String uri = Environment.getExternalStorageDirectory() + "/" + transcript.split("/")[2];
                System.out.println(uri);
                final File file = new File(uri);
                if (file.exists()) {

                    new Thread(new Runnable(){

                        public void run(){
                            /*
                            * Date: 2012
                            * Availability: http://www.coderzheaven.com/2013/03/06/download-pdf-file-open-android-installed-pdf-reader/
                            */
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri fileUri = Uri.fromFile(file);
                            System.out.println(fileUri);
//                            if (fileUri.substring(0, 7).matches("file://")) {
//                                fileUri =  fileUri.substring(7);
//                            }
                            Uri stuff = FileProvider.getUriForFile(LecturePage.this, BuildConfig.APPLICATION_ID + ".provider", file);
                            System.out.println(new File(String.valueOf(stuff)).exists());
                            System.out.println(stuff);
                            intent.setDataAndType(FileProvider.getUriForFile(LecturePage.this, BuildConfig.APPLICATION_ID + ".provider", file), "application/pdf");
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }).start();
                }
                else {
                    Intent intent = new Intent(LecturePage.this, TranscriptPage.class);
                    intent.putExtra("lecture", lecture);
                    intent.putExtra("course", course);
                    intent.putExtra("transcript", transcript);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
    }

    /*
     * Author: Hồng Thái
     * Date: 5/25/2015
     * Availability: http://www.devexchanges.info/2015/05/android-tip-combining-multiple.html
     */
    public void setHeight(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();

        int height = 0;
        int width = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for(int x = 0; x < listAdapter.getCount(); x++){
            View item = listAdapter.getView(x, null, listView);
            item.measure(width, View.MeasureSpec.UNSPECIFIED);
            height += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams newLayoutParams = listView.getLayoutParams();
        newLayoutParams.height = height + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(newLayoutParams);
        listView.requestLayout();
    }

    public void startChat(View view) {
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("course", course);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}

