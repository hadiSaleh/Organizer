package com.internshiporganizer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.internshiporganizer.Entities.ChatMessage;
import com.internshiporganizer.R;

import java.util.ArrayList;

public class ChatMessageAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private ArrayList<ChatMessage> objects;
    private long myId;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> messages, long myId) {
        objects = messages;
        this.myId = myId;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        final ChatMessage m = getChatMessage(position);

        if (!(m.getEmployeeId() == myId)) {
            view = lInflater.inflate(R.layout.list_item_message_received, parent, false);

            TextView msg = view.findViewById(R.id.message_text);
            msg.setText(m.getMessage());
            final TextView nick = view.findViewById(R.id.nick);
            nick.setText(m.getName());
            msg.setTextColor(Color.BLACK);
            return view;
        }

        view = lInflater.inflate(R.layout.list_item_message_sended, parent, false);

        TextView msg = view.findViewById(R.id.message_text_sended);
        msg.setText(m.getMessage());
        msg.setTextColor(Color.BLACK);

        return view;
    }

    private ChatMessage getChatMessage(int position) {
        return ((ChatMessage) getItem(position));
    }
}
