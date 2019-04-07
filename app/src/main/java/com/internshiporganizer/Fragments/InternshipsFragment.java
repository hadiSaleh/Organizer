package com.internshiporganizer.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.internshiporganizer.Constants;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.activities.InternshipActivity;
import com.internshiporganizer.Adapters.InternshipsAdapter;
import com.internshiporganizer.ApiClients.InternshipClient;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.R;
import com.internshiporganizer.activities.InternshipCreationActivity;

import java.util.ArrayList;
import java.util.List;

public class InternshipsFragment extends ListFragment implements Updatable<List<Internship>> {
    private InternshipsAdapter adapter;
    private ArrayList<Internship> internships;
    private InternshipClient internshipClient;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton fab;

    public InternshipsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
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
        adapter.notifyDataSetChanged();
    }

    private void setFAB() {
        fab = getView().findViewById(R.id.fabInternships);
        boolean administrator = sharedPreferences.getBoolean(Constants.IS_ADMINISTRATOR,false);
        if(!administrator){
            hideFAB();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO go to create
                Intent intent = new Intent(getActivity(), InternshipCreationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadInternships() {
        internships = new ArrayList<>();
        adapter = new InternshipsAdapter(getActivity(), internships);
        setListAdapter(adapter);

        Internship internship = new Internship();
        internship.setActive(true);
        internship.setDescription("Some description");
        internship.setTitle("Internship title");
        internship.setId(123);

        internships.add(internship);
        adapter.notifyDataSetChanged();
        //internshipClient.getAllByEmployee(123);
    }

    @SuppressLint("RestrictedApi")
    private void hideFAB() {
        fab.setVisibility(View.GONE);
    }
}
