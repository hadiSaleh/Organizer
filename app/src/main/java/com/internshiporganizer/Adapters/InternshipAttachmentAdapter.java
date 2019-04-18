package com.internshiporganizer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.internshiporganizer.Entities.InternshipAttachment;
import com.internshiporganizer.R;

import java.util.ArrayList;

public class InternshipAttachmentAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private ArrayList<InternshipAttachment> objects;

    public InternshipAttachmentAdapter(Context context, ArrayList<InternshipAttachment> attachments) {
        objects = attachments;
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
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_item_attachment, parent, false);
        }

        InternshipAttachment attachment = getAttachment(position);

        ((TextView) view.findViewById(R.id.listItem_attachment)).setText(attachment.getName());

        return view;
    }

    private InternshipAttachment getAttachment(int position) {
        return ((InternshipAttachment) getItem(position));
    }
}
