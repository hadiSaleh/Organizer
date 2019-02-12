package com.internshiporganizer.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.internshiporganizer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InternshipInfoFragment extends Fragment {


    public InternshipInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_internship_info, container, false);
    }

}
