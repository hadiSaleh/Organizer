package com.internshiporganizer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.internshiporganizer.ApiClients.GoalClient;
import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.R;

import java.util.List;

public class GoalActivity extends AppCompatActivity implements Updatable<List<Goal>> {
    private long goalId;
    private String internshipTitle;
    private GoalClient goalClient;

    private TextView titleET;
    private TextView descriptionET;
    private TextView placeET;
    private TextView deadlineET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        goalId = getIntent().getLongExtra("goalId", -1);
        internshipTitle = getIntent().getStringExtra("internshipTitle");
        goalClient = new GoalClient(getApplicationContext(), this);

        titleET = findViewById(R.id.goalActivity_title);
        descriptionET = findViewById(R.id.goalActivity_description);
        placeET = findViewById(R.id.goalActivity_place);
        deadlineET = findViewById(R.id.goalActivity_deadline);

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
        Goal goal = items.get(0);

        titleET.setText(goal.getTitle());
        descriptionET.setText(goal.getDescription());
        placeET.setText(goal.getPlace());
        deadlineET.setText(goal.getDeadline().substring(0, 10));
    }

    private void loadGoal() {
        goalClient.getById(goalId);
    }
}
