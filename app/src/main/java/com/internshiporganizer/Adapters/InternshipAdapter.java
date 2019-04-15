package com.internshiporganizer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.R;

import java.util.ArrayList;

public class InternshipAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private ArrayList<Internship> objects;

    public InternshipAdapter(Context context, ArrayList<Internship> internships) {
        objects = internships;
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
            view = lInflater.inflate(R.layout.list_item_internship, parent, false);
        }

        Internship internship = getInternship(position);

        ((TextView) view.findViewById(R.id.internshipTitle)).setText(internship.getTitle());
        ((TextView) view.findViewById(R.id.internshipDescription)).setText(internship.getDescription());
        TextView tv = view.findViewById(R.id.internshipCondition);
        if (internship.getActive()) {
            tv.setText(R.string.active);
            tv.setTextColor(Color.parseColor("#0277bd"));
        } else{
            tv.setText(R.string.closed);
            tv.setTextColor(Color.parseColor("#BDBDBD"));
        }

        return view;
    }

    private Internship getInternship(int position) {
        return ((Internship) getItem(position));
    }
}
