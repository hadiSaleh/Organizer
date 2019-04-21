package com.internshiporganizer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.internshiporganizer.Adapters.GoalAdapter;
import com.internshiporganizer.ApiClients.GoalClient;
import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;

import java.util.ArrayList;
import java.util.List;

public class EmployeeGoalsActivity extends AppCompatActivity {
    private String internshipTitle;
    private long internshipId;
    private long employeeId;

    private ArrayList<Goal> goals;
    private GoalAdapter adapter;

    private ListView goalsLV;

    private GoalClient goalClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_goals);
        internshipTitle = getIntent().getStringExtra("internshipTitle");
        internshipId = getIntent().getLongExtra("internshipId", -1);
        employeeId = getIntent().getLongExtra("employeeId", -1);

        setViews();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(internshipTitle);
    }

    private void setViews() {
        goalsLV = findViewById(R.id.employeeGoals_goals);
        goals = new ArrayList<>();
        adapter = new GoalAdapter(this, goals, false, true);
        goalsLV.setAdapter(adapter);

        goalsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Goal goal = (Goal) adapter.getItem(position);
                Intent intent = new Intent(EmployeeGoalsActivity.this, GoalActivity.class);
                intent.putExtra("goalId", goal.getId());
                intent.putExtra("internshipTitle", internshipTitle);
                intent.putExtra("isCompleted", true);
                startActivity(intent);
            }
        });

        goalClient = new GoalClient(this, new Updatable<List<Goal>>() {
            @Override
            public void update(List<Goal> goalList) {
                goals.addAll(goalList);
                adapter.notifyDataSetChanged();
            }
        });
        goalClient.getAllByEmployeeAndInternship(internshipId, employeeId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
