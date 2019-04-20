package com.internshiporganizer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.internshiporganizer.Entities.Request;
import com.internshiporganizer.R;

import java.util.ArrayList;

public class RequestAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private ArrayList<Request> objects;
    private boolean isAdmin;

    public RequestAdapter(Context context, ArrayList<Request> requests, boolean isAdmin) {
        objects = requests;
        this.isAdmin = isAdmin;
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
            view = lInflater.inflate(R.layout.list_item_goal, parent, false);
        }

        Request request = getRequest(position);

        ((TextView) view.findViewById(R.id.goalTitle)).setText(request.getTitle());
        ((TextView) view.findViewById(R.id.goalDescription)).setText(request.getDescription());
        TextView tv = view.findViewById(R.id.goalCondition);
        if (isAdmin) {
            tv.setVisibility(View.GONE);
        }

        if (!request.getCompleted()) {
            tv.setText(R.string.active);
            tv.setTextColor(Color.parseColor("#0277bd"));
        } else {
            tv.setText(R.string.closed);
            tv.setTextColor(Color.parseColor("#BDBDBD"));
        }

        return view;
    }

    private Request getRequest(int position) {
        return ((Request) getItem(position));
    }
}
