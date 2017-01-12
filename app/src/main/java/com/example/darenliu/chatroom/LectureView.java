package com.example.darenliu.chatroom;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;

public class LectureView extends FragmentActivity {

    FragmentTabHost fragmentTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("0").setIndicator("Lectures"), LectureFragment.class, null);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("1").setIndicator("Settings"), SettingsFragment.class, null);
    }

}
