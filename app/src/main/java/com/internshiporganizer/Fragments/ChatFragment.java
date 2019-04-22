package com.internshiporganizer.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.ArraySortedMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.internshiporganizer.Adapters.ChatMessageAdapter;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.ChatMessage;
import com.internshiporganizer.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatFragment extends ListFragment {
    private ArrayList<ChatMessage> messages;
    private long internshipId;
    private long myId;
    private String myName;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase fDB;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ChatMessageAdapter adapter;
    private EditText editText;
    private ImageButton sendButton;


    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(long internshipId) {
        ChatFragment f = new ChatFragment();
        Bundle bdl = new Bundle(2);
        bdl.putLong(Constants.ID, internshipId);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        internshipId = getArguments().getLong(Constants.ID);
        fDB = FirebaseDatabase.getInstance();

        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        checkPreferences();

        editText = getView().findViewById(R.id.edittext_chatbox);
        sendButton = getView().findViewById(R.id.button_chatbox_send);

        sendMessageButton();

        loadMessages();
    }

    private void sendMessageButton() {

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String messageToSend = editText.getText().toString();
                if (messageToSend.isEmpty()) {
                    return;
                }

                final ChatMessage message = new ChatMessage();
                message.setMessage(messageToSend);
                message.setEmployeeId(myId);
                message.setName(myName);

                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                final int hour = calendar.get(Calendar.HOUR);
                final int minute = calendar.get(Calendar.MINUTE);
                message.setDate(String.format("%d-%02d-%02d %02d:%02d", year, month, day, hour, minute));

                final DatabaseReference dbRef = fDB.getReference("chat/" + internshipId + "/numberOfMessages");
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int n = 0;
                        if (dataSnapshot.getValue() != null) {
                            n = Integer.parseInt(dataSnapshot.getValue().toString());
                        }

                        n++;
                        fDB.getReference("chat/" + internshipId + "/messages/" + n)
                                .setValue(message);
                        dbRef.setValue(n);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                editText.setText("");
            }
        });
    }

    private void checkPreferences() {
        myName = sharedPreferences.getString(Constants.NAME, "");
        myId = sharedPreferences.getLong(Constants.ID, 0);
    }

    private void loadMessages() {
        final Gson gson = new Gson();

        fDB.getReference("chat/" + internshipId + "/messages").orderByKey().limitToLast(16).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getActivity() == null) {
                    return;
                }

                messages = new ArrayList<>();
                adapter = new ChatMessageAdapter(getContext(), messages, myId);
                setListAdapter(adapter);

                ArrayList msgs = (ArrayList) dataSnapshot.getValue();

                if (msgs == null) {
                    return;
                }

                long k = 0;

                for (Object msg : msgs) {
                    if (msg == null) {
                        continue;
                    }

                    Map<String, Object> m = (Map<String, Object>) msg;
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setName((String) m.get("name"));
                    chatMessage.setEmployeeId((long) m.get("employeeId"));
                    chatMessage.setMessage((String) m.get("message"));
                    chatMessage.setDate((String) m.get("date"));

                    messages.add(chatMessage);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        messages = new ArrayList<>();
        adapter = new ChatMessageAdapter(getContext(), messages, myId);
        setListAdapter(adapter);


        adapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

}
