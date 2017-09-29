package com.shaban.darenliu.chatroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class LectureView extends FragmentActivity {

    FragmentTabHost fragmentTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        String phoneNumber = (String) intent.getSerializableExtra("phoneNumber");
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putString("phone", phoneNumber);

        fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("0").setIndicator("Lectures"), LectureFragment.class, bundle);
        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("1").setIndicator("Settings"), SettingsFragment.class, bundle);
    }

}
