package com.example.darenliu.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


/**
 * Created by Daren Liu on 7/25/2016.
 */
public class LecturePage extends Activity {
    Course course;
    User user;
    Lecture lecture;

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
                Intent intent = new Intent(LecturePage.this, TranscriptPage.class);
                intent.putExtra("lecture", lecture);
                intent.putExtra("course", course);
                intent.putExtra("transcript", lecture.getTranscriptUrl().get(indexOfTranscript));
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    //code borrowed from http://www.devexchanges.info/2015/05/android-tip-combining-multiple.html?m=1
    //Remember to cite this stuff
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

