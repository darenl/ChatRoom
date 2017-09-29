package com.shaban.darenliu.chatroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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

    private RecyclerView messagesView;
    private EditText inputMessageView;
    private ArrayList<Message> messages = new ArrayList<Message>();
    private RecyclerView.Adapter messageAdapter;
    private String nameOfUser;
    private SailsIOClient socket;
    private User user;
    private Course course;
    private Lecture lecture;

    public ChatFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        course = (Course) intent.getSerializableExtra("course");
        lecture = (Lecture) intent.getSerializableExtra("lecture");
        //System.out.println(user.getName());
        nameOfUser = user.getName();
        try {
            readMessagesFromFile("course" + course.getCourseId());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            messageAdapter = new MessageAdapter(activity, messages, user.getName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
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
//        Intent intent = new Intent(getActivity(), LecturePage.class);
//        intent.putExtra("course", course);
//        intent.putExtra("user", user);
//        intent.putExtra("lecture", lecture);
//        startActivity(intent);
        socket.socket.off("message", onNewMessage);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messagesView = (RecyclerView) view.findViewById(R.id.messages);
        messagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messagesView.setAdapter(messageAdapter);

        inputMessageView = (EditText) view.findViewById(R.id.message_input);
        inputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        inputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == nameOfUser) return;
                if (!socket.socket.isConnected()) return;
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

    private void addMessage(String username, String message) throws JSONException {
        Message newMsg = new Message(username, course, message);
        messages.add(newMsg);
        writeToFile(username, newMsg);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        scrollToBottom();
    }

    private void attemptSend() throws Exception {
        if (null == nameOfUser) return;
        if (!socket.socket.isConnected()) return;

        String message = inputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            inputMessageView.requestFocus();
            return;
        }

        inputMessageView.setText("");
        addMessage(user.getName(), message);
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
        JSONObject author = new JSONObject();
        JSONObject group = new JSONObject();

        author.put("firstName", user.getFirstName());
        author.put("lastName", user.getLastName());
        author.put("phone", user.getPhone());
        group.put("serial_number", lecture.getSerialNumber());
        group.put("description", lecture.getLectureDescription());
        group.put("transcript_url", lecture.getTranscriptUrl());
        group.put("id", lecture.getLectureId());
        group.put("course", course.getCourseId());
        data.put("author", author);
        data.put("group", group);
        data.put("content", message);
        return data;
    }

    private void leave() {
        socket.socket.Disconnect();
//        Intent intent = new Intent(getActivity(), LecturePage.class);
//        intent.putExtra("course", course);
//        intent.putExtra("user", user);
//        intent.putExtra("lecture", lecture);
//        startActivity(intent);
    }

    private void scrollToBottom() {
        messagesView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONObject author;
                    String username;
                    String message;
                    try {
                        author = data.getJSONObject("author");
                        username = author.getString("firstName") + " " + author.getString("lastName");
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

    private void readMessagesFromFile(String url) throws IOException, JSONException, ExecutionException, InterruptedException {
        String uri = getActivity().getFilesDir().toString();
        final File file = new File(uri, url);
        if(!file.exists()) {
            file.createNewFile();
            new GetAllMessages().execute().get();
        }
        else{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            JSONObject jsonObject;
            String line;
            Message message;
            while ((line = bufferedReader.readLine()) != null) {
                jsonObject = new JSONObject(line);
                message = new Message((String) jsonObject.get("user"), course, (String) jsonObject.get("message"));
                messages.add(message);
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
        final File file = new File(uri, "course" + course.getCourseId());
        try {
            FileOutputStream fileOutputStream;
            OutputStreamWriter outputStreamWriter;
            if(file.exists()) {
                fileOutputStream = getContext().openFileOutput(file.getName(), Context.MODE_APPEND);
                outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.append(jsonToFile + '\n');
            }
            else {
                fileOutputStream = getContext().openFileOutput(file.getName(), Context.MODE_PRIVATE);
                outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.write(jsonToFile + '\n');
            }
            outputStreamWriter.close();

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
        private String url = "https://shaban.rit.albany.edu/messages";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            // Showing progress loading dialog
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setMessage("Loading all messages...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Making a request to url and getting response
            String jsonStr = null;
            HashMap<Integer, Message> messageMap;
            try {
                jsonStr = JsonReader.readJsonFromUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            messages = JsonReader.ParseJSONMessage(jsonStr, course);
            for(int x = 0; x < messages.size() - 1; x++){
                int min = x;
                for(int y = x+1; y < messages.size(); y++){
                    if(messages.get(y).getId() < messages.get(min).getId()) {
                        min = y;
                    }

                }
                Message temp = messages.get(x);
                messages.set(x, messages.get(min));
                messages.set(min, temp);
            }

            for (Message msg: messages) {
                try {
                    writeToFile(msg.getUsername(), msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

        }

    }
}

