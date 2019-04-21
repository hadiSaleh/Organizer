package com.internshiporganizer.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.internshiporganizer.Adapters.RequestAdapter;
import com.internshiporganizer.ApiClients.RequestClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Request;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.activities.RequestActivity;
import com.internshiporganizer.activities.RequestCreationActivity;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends ListFragment implements Updatable<List<Request>> {
    private long internshipId;
    private boolean isCompleted;
    private RequestAdapter adapter;
    private ArrayList<Request> requests;
    private RequestClient requestClient;
    private String internshipTitle;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refreshRequests;

    private SharedPreferences sharedPreferences;

    public RequestsFragment() {
        // Required empty public constructor
    }

    public static RequestsFragment newInstance(long internshipId, boolean isCompleted) {
        RequestsFragment f = new RequestsFragment();
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

        refreshRequests = getView().findViewById(R.id.refreshRequests);
        refreshRequests.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRequests();
            }
        });

        requestClient = new RequestClient(getContext(), this);
        loadRequests();

        setFAB();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Request request = (Request) adapter.getItem(position);
        Intent intent = new Intent(getActivity(), RequestActivity.class);
        intent.putExtra("requestId", request.getId());
        intent.putExtra("internshipTitle", internshipTitle);
        intent.putExtra("isCompleted", isCompleted);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void update(List<Request> items) {
        requests.addAll(items);
        adapter.notifyDataSetChanged();
        refreshRequests.setRefreshing(false);
    }

    public void setInternshipTitle(String internshipTitle) {
        this.internshipTitle = internshipTitle;
    }

    private void setFAB() {
        fab = getView().findViewById(R.id.fabRequests);
        boolean administrator = sharedPreferences.getBoolean(Constants.IS_ADMINISTRATOR, false);
        if (!administrator || isCompleted) {
            hideFAB();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RequestCreationActivity.class);
                intent.putExtra("internshipId", internshipId);
                startActivity(intent);
            }
        });
    }

    private void loadRequests() {
        requests = new ArrayList<>();
        boolean isAdmin = sharedPreferences.getBoolean(Constants.IS_ADMINISTRATOR, false);
        adapter = new RequestAdapter(getActivity(), requests, isAdmin);
        setListAdapter(adapter);

        long employeeId = sharedPreferences.getLong(Constants.ID, 0);
        requestClient.getAllByEmployeeAndInternship(internshipId, employeeId);
    }

    @SuppressLint("RestrictedApi")
    private void hideFAB() {
        fab.setVisibility(View.GONE);
    }
}
