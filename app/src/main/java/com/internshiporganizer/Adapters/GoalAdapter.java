package com.internshiporganizer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.R;

import java.util.ArrayList;

public class GoalAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private ArrayList<Goal> objects;
    private boolean isAdmin;
    private boolean isInternshipCompleted;

    public GoalAdapter(Context context, ArrayList<Goal> goals, boolean isAdmin) {
        this(context, goals, isAdmin, false);
    }

    public GoalAdapter(Context context, ArrayList<Goal> goals, boolean isAdmin, boolean isInternshipCompleted) {
        objects = goals;
        this.isAdmin = isAdmin;
        this.isInternshipCompleted = isInternshipCompleted;
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

        Goal goal = getGoal(position);

        ((TextView) view.findViewById(R.id.goalTitle)).setText(goal.getTitle());
        ((TextView) view.findViewById(R.id.goalDescription)).setText(goal.getDescription());
        TextView tv = view.findViewById(R.id.goalCondition);

        if (isInternshipCompleted) {
            if (!goal.getCompleted()) {
                tv.setText(R.string.not_fulfilled);
                tv.setTextColor(Color.parseColor("#BDBDBD"));
            } else {
                tv.setText(R.string.closed);
                tv.setTextColor(Color.parseColor("#0277bd"));
            }

            return view;
        }

        if (isAdmin) {
            tv.setVisibility(View.GONE);
        }

        if (!goal.getCompleted()) {
            tv.setText(R.string.active);
            tv.setTextColor(Color.parseColor("#0277bd"));
        } else {
            tv.setText(R.string.closed);
            tv.setTextColor(Color.parseColor("#BDBDBD"));
        }

        return view;
    }

    private Goal getGoal(int position) {
        return ((Goal) getItem(position));
    }
}
