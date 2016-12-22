package com.example.darenliu.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView;

    private String mUsername;

    private Socket socket;

    Intent name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = getIntent();
        String groupId = name.getStringExtra("groupId");

        {
            try {
                socket = IO.socket("https://shaban.rit.albany.edu//");

            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        // Set up the login form.
        final String username = (String) name.getSerializableExtra("user");
        TextView textView = (TextView) findViewById(R.id.username);
        textView.setText(username);
//        mUsernameView = (EditText) findViewById(R.id.username_input);
//        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    try {
//                        attemptLogin(username);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin(username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("login", onLogin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.off("login", onLogin);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(String username) throws JSONException{
        // Reset errors.
//        mUsernameView.setError(null);
//        // Store values at the time of the login attempt.
//        //String username = mUsernameView.getText().toString().trim();
//        // Check for a valid username.
//        if (TextUtils.isEmpty(username)) {
//            // There was an error; don't attempt login and focus the first
//            // form field with an error.
//            mUsernameView.setError(getString(R.string.error_field_required));
//            mUsernameView.requestFocus();
//            return;
//        }
        mUsername = username;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("university_id", "0");
            jsonObject.put("arabic_name", "");
            jsonObject.put("name", mUsername);
            jsonObject.put("whatsapp", "1111");
            jsonObject.put("email", null);
            jsonObject.put("location", null);
            jsonObject.put("id", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        socket.emit("user", jsonObject);
        // perform the user login attempt.
        socket.emit("user", jsonObject);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
//            JSONObject data = (JSONObject) args[0];
//
//            int numUsers;
//            try {
//                //numUsers = data.getInt("numUsers");
//            } catch (JSONException e) {
//                return;
//            }

            Intent intent = new Intent();
            intent.putExtra("username", mUsername);
            //intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
}




