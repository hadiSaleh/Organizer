package com.internshiporganizer.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.internshiporganizer.Adapters.EmployeeAdapter;
import com.internshiporganizer.ApiClients.InternshipParticipantsClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.Entities.InternshipParticipant;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.activities.EmployeeGoalsActivity;

import java.util.ArrayList;
import java.util.List;

public class GoalEmployeesFragment extends ListFragment {
    private EmployeeAdapter adapter;
    private ArrayList<Employee> employees;
    private InternshipParticipantsClient client;
    private SharedPreferences sharedPreferences;

    private long internshipId;
    private String internshipTitle;

    public GoalEmployeesFragment() {
    }

    public static GoalEmployeesFragment newInstance(long internshipId) {
        GoalEmployeesFragment f = new GoalEmployeesFragment();
        Bundle bdl = new Bundle(2);
        bdl.putLong(Constants.ID, internshipId);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        internshipId = getArguments().getLong(Constants.ID);

        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);

        client = new InternshipParticipantsClient(getContext(), new Updatable<List<InternshipParticipant>>() {
            @Override
            public void update(List<InternshipParticipant> internshipParticipants) {
                for (InternshipParticipant participant : internshipParticipants) {
                    Employee employee = participant.getEmployee();
                    if (!employee.getAdministrator()) {
                        employees.add(employee);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        loadEmployees();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Employee employee = (Employee) adapter.getItem(position);
        Intent intent = new Intent(getActivity(), EmployeeGoalsActivity.class);
        intent.putExtra("employeeId", employee.getId());
        intent.putExtra("internshipTitle", internshipTitle);
        intent.putExtra("internshipId", internshipId);

        startActivity(intent);
    }

    private void loadEmployees() {
        employees = new ArrayList<>();
        adapter = new EmployeeAdapter(getContext(), employees);
        setListAdapter(adapter);

        client.getAllByInternship(internshipId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal_employees, container, false);
    }

    public void setInternshipTitle(String internshipTitle) {
        this.internshipTitle = internshipTitle;
    }
}
