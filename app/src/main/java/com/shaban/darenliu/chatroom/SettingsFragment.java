package com.shaban.darenliu.chatroom;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

        if(user == null)
            new ConfirmUser().execute();

        name = (TextView) view.findViewById(R.id.name);
        phone = (TextView) view.findViewById(R.id.phone);
        if(user != null) {
            name.setText("User: " + user.getName());
            phone.setText("Phone Number: " + user.getPhone());
        }
        else{
            Toast toast = new Toast(getContext());
            toast.makeText(getContext(), "Not a registered user", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

    }

    private class ConfirmUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading user settings...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            try {
                jsonStr = JsonReader.readJsonFromUrl("https://shaban.rit.albany.edu/users");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            user = JsonReader.ParseJSONUser(jsonStr, getActivity());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            name.setText("User: " + user.getName());
            phone.setText("Phone Number: " + user.getPhone());
        }

    }
}
