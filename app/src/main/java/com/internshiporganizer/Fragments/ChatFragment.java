package com.internshiporganizer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.internshiporganizer.Adapters.ChatMessageAdapter;
import com.internshiporganizer.Entities.ChatMessage;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.R;

import java.util.ArrayList;

public class ChatFragment extends ListFragment {
    private ArrayList<ChatMessage> messages;
    private long id;

    private ChatMessageAdapter adapter;
    private EditText editText;
    private ImageButton sendButton;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editText = getView().findViewById(R.id.edittext_chatbox);
        sendButton = getView().findViewById(R.id.button_chatbox_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        loadMessages();
    }

    private void loadMessages() {
        messages = new ArrayList<>();
        adapter = new ChatMessageAdapter(getContext(), messages, 1);
        setListAdapter(adapter);

        ChatMessage m1 = new ChatMessage();
        Employee e1 = new Employee();
        e1.setId(1);
        m1.setEmployee(e1);
        m1.setMessage("Hi");

        ChatMessage m2 = new ChatMessage();
        Employee e2 = new Employee();
        e2.setId(2);
        m2.setEmployee(e2);
        m2.setMessage("Hello");

        ChatMessage m3 = new ChatMessage();
        m3.setEmployee(e2);
        m3.setMessage("Whassup");

        messages.add(m1);
        messages.add(m2);
        messages.add(m3);

        adapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

}
