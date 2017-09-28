package com.shaban.darenliu.chatroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by dliu7 on 1/6/2017.
 */

public class SettingsFragment extends Fragment {

    public User user;
    TextView name;
    TextView phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent = getActivity().getIntent();
        //user = (User) intent.getSerializableExtra("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Bundle arg = getArguments();
        user = (User) arg.getSerializable("user");
        name = (TextView) view.findViewById(R.id.name);
        phone = (TextView) view.findViewById(R.id.phone);
        if(user != null) {
            name.setText("User: " + user.getName());
            phone.setText("Phone Number: " + user.getPhone());
        }
//        else{
//            Toast toast = new Toast(getContext());
//            toast.makeText(getContext(), "Not a registered user", Toast.LENGTH_SHORT).show();
//        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

    }
}
