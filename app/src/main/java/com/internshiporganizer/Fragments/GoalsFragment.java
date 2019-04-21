package com.internshiporganizer.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.internshiporganizer.Adapters.GoalAdapter;
import com.internshiporganizer.ApiClients.GoalClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.activities.GoalActivity;
import com.internshiporganizer.activities.GoalCreationActivity;

import java.util.ArrayList;
import java.util.List;

public class GoalsFragment extends ListFragment implements Updatable<List<Goal>> {
    private long internshipId;
    private boolean isCompleted;
    private GoalAdapter adapter;
    private ArrayList<Goal> goals;
    private GoalClient goalClient;
    private String internshipTitle;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refreshGoals;

    private SharedPreferences sharedPreferences;

    public GoalsFragment() {
        // Required empty public constructor
    }

    public static GoalsFragment newInstance(long internshipId, boolean isCompleted) {
        GoalsFragment f = new GoalsFragment();
        Bundle bdl = new Bundle(2);
        bdl.putLong(Constants.ID, internshipId);
        bdl.putBoolean("isCompleted", isCompleted);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        internshipId = getArguments().getLong(Constants.ID);
        isCompleted = getArguments().getBoolean("isCompleted");

        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);

        refreshGoals = getView().findViewById(R.id.refreshGoals);
        refreshGoals.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGoals();
            }
        });

        goalClient = new GoalClient(getContext(), this);
        loadGoals();

        setFAB();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Goal goal = (Goal) adapter.getItem(position);
        Intent intent = new Intent(getActivity(), GoalActivity.class);
        intent.putExtra("goalId", goal.getId());
        intent.putExtra("internshipTitle", internshipTitle);
        intent.putExtra("isCompleted", isCompleted);
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
        refreshGoals.setRefreshing(false);
    }

    public void setInternshipTitle(String internshipTitle) {
        this.internshipTitle = internshipTitle;
    }

    private void setFAB() {
        fab = getView().findViewById(R.id.fabGoals);
        boolean administrator = sharedPreferences.getBoolean(Constants.IS_ADMINISTRATOR, false);
        if (!administrator || isCompleted) {
            hideFAB();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GoalCreationActivity.class);
                intent.putExtra("internshipId", internshipId);
                startActivity(intent);
            }
        });
    }

    private void loadGoals() {
        goals = new ArrayList<>();
        boolean isAdmin = sharedPreferences.getBoolean(Constants.IS_ADMINISTRATOR, false);
        adapter = new GoalAdapter(getActivity(), goals, isAdmin);
        setListAdapter(adapter);

        long employeeId = sharedPreferences.getLong(Constants.ID, 0);
        goalClient.getAllByEmployeeAndInternship(internshipId, employeeId);
    }

    @SuppressLint("RestrictedApi")
    private void hideFAB() {
        fab.setVisibility(View.GONE);
    }
}
