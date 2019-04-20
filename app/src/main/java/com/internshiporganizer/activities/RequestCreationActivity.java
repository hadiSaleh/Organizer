package com.internshiporganizer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.internshiporganizer.ApiClients.EmployeeClient;
import com.internshiporganizer.ApiClients.RequestClient;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.Entities.Request;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;

import java.util.List;

public class RequestCreationActivity extends AppCompatActivity {
    private long internshipId;

    private EditText titleET;
    private EditText descriptionET;
    private Button finishButton;

    private EmployeeClient employeeClient;
    private RequestClient requestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_creation);

        internshipId = getIntent().getLongExtra("internshipId", 0);

        titleET = findViewById(R.id.requestCreation_title);
        descriptionET = findViewById(R.id.requestCreation_description);
        finishButton = findViewById(R.id.requestCreation_buttonFinish);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("New request");

        setListeners();
    }

    private void setListeners() {
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleET.getText().toString();
                String description = descriptionET.getText().toString();

                if (title.equals("")) {
                    Toast.makeText(RequestCreationActivity.this, "Title can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (description.equals("")) {
                    Toast.makeText(RequestCreationActivity.this, "Description can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                String[] lines = description.split("\r\n|\r|\n");
                if (lines.length > 10) {
                    Toast.makeText(RequestCreationActivity.this, "Description should not exceed 10 lines!", Toast.LENGTH_LONG).show();
                    return;
                }

                Request newRequest = new Request();
                newRequest.setTitle(title);
                newRequest.setDescription(description);

                createRequest(newRequest);
            }
        });
    }

    private void createRequest(final Request newRequest) {
        final Internship internship = new Internship();
        internship.setId(internshipId);

        employeeClient = new EmployeeClient(this, new Updatable<List<Employee>>() {
            @Override
            public void update(List<Employee> employees) {
                requestClient.createRequest(newRequest, internship, employees);
                Toast.makeText(getApplicationContext(), "Request created!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        requestClient = new RequestClient(this, new Updatable<List<Request>>() {
            @Override
            public void update(List<Request> requests) {
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
