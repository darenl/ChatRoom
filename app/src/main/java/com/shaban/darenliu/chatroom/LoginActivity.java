package com.shaban.darenliu.chatroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by dliu7 on 9/28/2017.
 */

public class LoginActivity extends Activity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button btn = (Button) findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phone = (EditText) findViewById(R.id.phone);
                if(phone.getText().toString() == ""){
                    Toast.makeText(getApplicationContext(), "Enter your phone number", Toast.LENGTH_LONG);
                }
                else{
                    new ConfirmUser(phone.getText().toString()).execute();
                }
            }
        });
    }
    private class ConfirmUser extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        String phoneNumber;

        public ConfirmUser(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            try {
                jsonStr = JsonReader.readJsonFromUrl("https://shaban.rit.albany.edu/users/" + phoneNumber);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            user = JsonReader.ParseJSONUser(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            if (user != null) {
                Intent intent = new Intent(LoginActivity.this.getBaseContext(), MainActivity.class);
                intent.putExtra("user", user);
                LoginActivity.this.startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(), "User not registered", Toast.LENGTH_LONG);
            }
        }
    }
}
