package com.internshiporganizer.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.internshiporganizer.Adapters.GoalsAdapter;
import com.internshiporganizer.ApiClients.GoalClient;
import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.R;
import com.internshiporganizer.activities.GoalActivity;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends ListFragment implements Updatable<Goal> {
    private GoalsAdapter adapter;
    private ArrayList<Goal> goals;
    private GoalClient goalClient;
    private String internshipTitle;

    public GoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        goalClient = new GoalClient(getContext(), this);
        loadGoals();

        FloatingActionButton fab = getView().findViewById(R.id.fabGoals);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO go to create
//                Intent intent = new Intent(getActivity(), NoteCreatingActivity.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Goal goal = (Goal) adapter.getItem(position);
        Intent intent = new Intent(getActivity(),GoalActivity.class);
        intent.putExtra("goalId", goal.getId());
        intent.putExtra("internshipTitle", internshipTitle);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void update(List<Goal> items) {
        goals.addAll(items);
        adapter.notifyDataSetChanged();
    }

    public void setInternshipTitle(String internshipTitle) {
        this.internshipTitle = internshipTitle;
    }

    private void loadGoals() {
        goals = new ArrayList<>();
        adapter = new GoalsAdapter(getActivity(), goals);
        setListAdapter(adapter);

        Goal goal = new Goal();
        goal.setTitle("Goal 1");
        goal.setDescription("Goal 1 is very important");
        goals.add(goal);

        Goal goal12 = new Goal();
        goal12.setTitle("Goal 2");
        goal12.setCompleted(true);
        goals.add(goal12);

        adapter.notifyDataSetChanged();

        //goalClient.getAllByEmployeeAndInternship(123,321);
    }
}
