package com.internshiporganizer.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.internshiporganizer.ApiClients.EmployeeClient;
import com.internshiporganizer.ApiClients.GoalClient;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;

import java.util.Calendar;
import java.util.List;

public class GoalCreationActivity extends AppCompatActivity {
    private long internshipId;

    private EditText titleET;
    private EditText descriptionET;
    private EditText placeET;
    private TextView deadlineET;
    private Button finishButton;

    private EmployeeClient employeeClient;
    private GoalClient goalClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_creation);
        internshipId = getIntent().getLongExtra("internshipId", 0);

        titleET = findViewById(R.id.goalCreation_title);
        descriptionET = findViewById(R.id.goalCreation_description);
        placeET = findViewById(R.id.goalCreation_place);
        deadlineET = findViewById(R.id.goalCreation_deadline);
        finishButton = findViewById(R.id.goalCreation_buttonFinish);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("New goal");

        setListeners();
    }

    private void setListeners() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        deadlineET.setText(String.format("%d-%02d-%02d", year, month, day));

        deadlineET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(GoalCreationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        deadlineET.setText(String.format("%d-%02d-%02d", year, month, dayOfMonth));
                    }
                }, year, month, day);
                dialog.show();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleET.getText().toString();
                String description = descriptionET.getText().toString();
                String place = placeET.getText().toString();

                if (title.equals("")) {
                    Toast.makeText(GoalCreationActivity.this, "Title can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (description.equals("")) {
                    Toast.makeText(GoalCreationActivity.this, "Description can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                String[] lines = description.split("\r\n|\r|\n");
                if (lines.length > 10) {
                    Toast.makeText(GoalCreationActivity.this, "Description should not exceed 2 lines!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (place.equals("")) {
                    Toast.makeText(GoalCreationActivity.this, "Place can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                Goal newGoal = new Goal();
                newGoal.setTitle(title);
                newGoal.setDescription(description);
                newGoal.setPlace(place);
                newGoal.setDeadline(deadlineET.getText().toString());

                createGoal(newGoal);

            }
        });
    }

    private void createGoal(final Goal newGoal) {
        final Internship internship = new Internship();
        internship.setId(internshipId);

        employeeClient = new EmployeeClient(this, new Updatable<List<Employee>>() {
            @Override
            public void update(List<Employee> employees) {
                goalClient.createGoal(newGoal, internship, employees);
                Toast.makeText(getApplicationContext(), "Goal created!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        goalClient = new GoalClient(this, new Updatable<List<Goal>>() {
            @Override
            public void update(List<Goal> goals) {
            }
        });

        employeeClient.getByInternship(internshipId);
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
