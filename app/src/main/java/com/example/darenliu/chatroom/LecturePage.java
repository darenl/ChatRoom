package com.example.darenliu.chatroom;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
                String uri = getApplicationContext().getFilesDir().toString();
                final File file = new File(uri, transcript.split("/")[2]);
                if (file.exists()) {
                /**
                    * Author: Naveed Ahmad
                    * Date: 7/2/16
                    * Availability: http://stackoverflow.com/questions/38159187/opening-pdf-file-error-this-file-could-not-be-accessed-check-the-location-or-th
                */
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri fileUri = Uri.fromFile(file);
                    String filePath = file.getAbsolutePath();
                    Uri uriFile = Uri.parse("content://com.example.darenliu.chatroom/" + filePath);
                    intent.setDataAndType(uriFile, "application/pdf");
                    Intent intentPDF = Intent.createChooser(intent, "Choose Pdf Application");
                    startActivity(intentPDF);
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
        intent.putExtra("lecture", lecture);
        startActivity(intent);
    }
}

