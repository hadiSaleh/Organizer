package com.internshiporganizer.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.internshiporganizer.Adapters.InternshipsAdapter;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.R;

import java.util.ArrayList;

public class InternshipsFragment extends ListFragment {
    private InternshipsAdapter adapter;
    private ArrayList<Internship> internships;

    public InternshipsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadInternships();

        FloatingActionButton fab = getView().findViewById(R.id.fabInternships);

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
        final Internship n = (Internship) adapter.getItem(position);
    }

    private void loadInternships(){
        internships = new ArrayList<>();
        adapter = new InternshipsAdapter(getActivity(), internships);
        setListAdapter(adapter);
        // TODO api call
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_internships, container, false);
    }
}
