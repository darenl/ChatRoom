package com.shaban.darenliu.chatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.view.Menu;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends FragmentActivity {

    FragmentTabHost fragmentTabHost;
    User user;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        phoneNumber = (String) intent.getSerializableExtra("phoneNumber");
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putString("phone", phoneNumber);

//        final File file = new File(uri, url);
//        if(!file.exists()) {
//            file.createNewFile();
//            new MainActivity.GetAllMessages().execute().get();
//        }

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("0").setIndicator("Courses"), CourseFragment.class, bundle);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("1").setIndicator("Settings"), SettingsFragment.class, bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.courses_list, menu);
        return true;
    }

}
