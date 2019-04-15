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

import com.internshiporganizer.Constants;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.activities.InternshipActivity;
import com.internshiporganizer.Adapters.InternshipAdapter;
import com.internshiporganizer.ApiClients.InternshipClient;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.R;
import com.internshiporganizer.activities.InternshipCreationActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InternshipsFragment extends ListFragment implements Updatable<List<Internship>> {
    private InternshipAdapter adapter;
    private ArrayList<Internship> internships;
    private InternshipClient internshipClient;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refreshInternships;

    public InternshipsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);

        refreshInternships = getView().findViewById(R.id.refreshInternships);
        refreshInternships.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadInternships();
            }
        });

        internshipClient = new InternshipClient(getContext(), this);
        loadInternships();

        setFAB();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Internship internship = (Internship) adapter.getItem(position);
        Intent intent = new Intent(getActivity(), InternshipActivity.class);
        intent.putExtra("internshipTitle", internship.getTitle());
        intent.putExtra("internshipId", internship.getId());

        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_internships, container, false);
    }

    @Override
    public void update(List<Internship> items) {
        internships.addAll(items);
        Collections.sort(internships, new Comparator<Internship>() {
            @Override
            public int compare(Internship i1, Internship i2) {
                return Boolean.compare(i2.getActive(), i1.getActive());
            }
        });

        adapter.notifyDataSetChanged();
        refreshInternships.setRefreshing(false);
    }

    private void setFAB() {
        fab = getView().findViewById(R.id.fabInternships);
        boolean administrator = sharedPreferences.getBoolean(Constants.IS_ADMINISTRATOR, false);
        if (!administrator) {
            hideFAB();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InternshipCreationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadInternships() {
        internships = new ArrayList<>();
        adapter = new InternshipAdapter(getActivity(), internships);
        setListAdapter(adapter);

        long employeeId = sharedPreferences.getLong(Constants.ID, 0);
        internshipClient.getAllByEmployee(employeeId);
    }

    @SuppressLint("RestrictedApi")
    private void hideFAB() {
        fab.setVisibility(View.GONE);
    }
}
