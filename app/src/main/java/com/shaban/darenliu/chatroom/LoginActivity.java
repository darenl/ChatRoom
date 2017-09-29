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

import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import static java.security.AccessController.getContext;

/**
 * Created by dliu7 on 9/28/2017.
 */

public class LoginActivity extends Activity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = LoginActivity.this.getFilesDir().toString();
        File file = new File(url, "userInfo");
        if(file.exists()){
            try {
                FileInputStream inputStream = new FileInputStream(file);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                JSONObject json = new JSONObject(new String(buffer, "UTF-8"));
                user = new User(json.getString("firstName"), json.getString("lastName"), json.getString("phone"));
                Intent intent = new Intent(LoginActivity.this.getBaseContext(), MainActivity.class);
                intent.putExtra("user", user);
                LoginActivity.this.startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            setContentView(R.layout.login);

            Button btn = (Button) findViewById(R.id.button2);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText phone = (EditText) findViewById(R.id.phone);
                    if (phone.getText().toString() == "") {
                        Toast.makeText(getApplicationContext(), "Enter your phone number", Toast.LENGTH_LONG);
                    } else {
                        new ConfirmUser(phone.getText().toString()).execute();
                    }
                }
            });
        }
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
                writeToFile(user);
                Intent intent = new Intent(LoginActivity.this.getBaseContext(), MainActivity.class);
                intent.putExtra("user", user);
                LoginActivity.this.startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "User not registered", Toast.LENGTH_LONG);
            }
        }

        private void writeToFile(User user) {
            JSONObject json = new JSONObject();
            try {
                json.put("firstName", user.getFirstName());
                json.put("lastName", user.getLastName());
                json.put("phone", user.getPhone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String jsonToFile = json.toString();
            String uri = LoginActivity.this.getFilesDir().toString();
            final File file = new File(uri, "userInfo");
            try {
                FileOutputStream fileOutputStream;
                OutputStreamWriter outputStreamWriter;
                fileOutputStream = LoginActivity.this.getBaseContext().openFileOutput(file.getName(), Context.MODE_PRIVATE);
                outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.write(jsonToFile + '\n');
                outputStreamWriter.close();

                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
