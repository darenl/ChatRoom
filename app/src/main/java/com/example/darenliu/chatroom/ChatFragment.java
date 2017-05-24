package com.example.darenliu.chatroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.emitter.Emitter;


/**
 * A chat fragment containing messages view and input form.
 * Using library from following github link
 * Author: Naoyuki Kanezawa
 * Date: 2016
 * Availability: https://github.com/nkzawa/socket.io-android-chat/blob/master/app/src/main/java/com/github/nkzawa/socketio/androidchat/MainFragment.java
 */
public class ChatFragment extends Fragment {

    private RecyclerView mMessagesView;
    private ArrayList<Message> oldMessages = new ArrayList<Message>();
    private EditText mInputMessageView;
    private ArrayList<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private String nameOfUser;
    private SailsIOClient socket;
    private User user;
    private Course course;
    private Lecture lecture;
    private static String url = "https://shaban.rit.albany.edu/lecture";

    public ChatFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            readMessagesFromFile("course" + course.getCourseId());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new MessageAdapter(activity, mMessages);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        course = (Course) intent.getSerializableExtra("course");
        lecture = (Lecture) intent.getSerializableExtra("lecture");
        nameOfUser = user.getName();
        setHasOptionsMenu(true);

        socket = new SailsIOClient("https://shaban.rit.albany.edu", course.getCourseId());
        socket.socket.on("message", onNewMessage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        socket.socket.Disconnect();
        Intent intent = new Intent(getActivity(), LecturePage.class);
        intent.putExtra("course", course);
        intent.putExtra("user", user);
        startActivity(intent);
        socket.socket.off("message", onNewMessage);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    try {
                        attemptSend();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == nameOfUser) return;
                if (!socket.socket.isConnected()) return;

                if (!mTyping) {
                    mTyping = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    attemptSend();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addMessage(String message) throws JSONException {
        Message newMsg = new Message(user, course, message);
        mMessages.add(newMsg);
        writeToFile(user.getName(), newMsg);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addMessage(String username, String message) throws JSONException {
        Message newMsg = new Message(username, course, message);
        mMessages.add(newMsg);
        writeToFile(username, newMsg);
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void attemptSend() throws Exception {
        if (null == nameOfUser) return;
        if (!socket.socket.isConnected()) return;

        mTyping = false;

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }

        mInputMessageView.setText("");
        addMessage(message);
        JSONObject jsonObject = emitMessage(message);

        // perform the sending message attempt.
        socket.socket.post("/messages", jsonObject, new Ack() {
            @Override
            public void call(Object... args) {
                System.out.println("Message is sent");
            }
        });
    }

    public JSONObject emitMessage(String message) throws Exception{
        JSONObject data = new JSONObject();
        data.put("author", 1);
        data.put("course", 1);
        data.put("content", message);
        return data;
    }

    private void leave() {
        socket.socket.Disconnect();
        Intent intent = new Intent(getActivity(), LecturePage.class);
        intent.putExtra("course", course);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void scrollToBottom() {
        mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("New message has been sent");
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("author");
                        message = data.getString("content");
                    } catch (JSONException e) {
                        return;
                    }
                    if(!username.equals(user.getName()))
                        try {
                            addMessage(username, message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            });
        }
    };

    private void readMessagesFromFile(String url) throws IOException, JSONException {
        String uri = getActivity().getFilesDir().toString();
        final File file = new File(uri, url);

        if(file.exists())
            new GetAllMessages().execute();

        else{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            JSONObject jsonObject;

            while (bufferedReader.readLine() != null) {
                jsonObject = new JSONObject(bufferedReader.readLine());
                mMessages.add(new Message((String) jsonObject.get("user"), course, (String) jsonObject.get("message")));
            }
            bufferedReader.close();
        }
    }

    private void writeToFile(String username, Message message) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("user", username);
        json.put("message", message.getContent());
        String jsonToFile = json.toString();
        String uri = getActivity().getFilesDir().toString();
        final File file = new File(uri, "course" + course.getCourseName());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fileOutputStream);
            if(file.exists())
                myOutWriter.append(jsonToFile + '\n');
            else
                myOutWriter.write(jsonToFile + '\n');
            myOutWriter.close();

            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class GetAllMessages extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading all messages...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            if(oldMessages == null) {
                try {
                    jsonStr = JsonReader.readJsonFromUrl(url + "/messages");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                oldMessages = JsonReader.ParseJSONMessage(jsonStr);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }

    }
}

