package com.internshiporganizer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.internshiporganizer.ApiClients.GoalClient;
import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.R;

import java.util.List;

public class GoalActivity extends AppCompatActivity implements Updatable<List<Goal>> {
    private long goalId;
    private String internshipTitle;
    private GoalClient goalClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        goalId = getIntent().getLongExtra("goalId", -1);
        internshipTitle = getIntent().getStringExtra("internshipTitle");
        goalClient = new GoalClient(getApplicationContext(),this);
        loadGoal();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(internshipTitle);
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

    @Override
    public void update(List<Goal> items) {

    }

    private void loadGoal() {
        //goalClient.getById(goalId);
    }
}
