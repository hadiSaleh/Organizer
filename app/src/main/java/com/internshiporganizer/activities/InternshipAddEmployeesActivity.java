package com.internshiporganizer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.internshiporganizer.Adapters.EmployeeCheckableAdapter;
import com.internshiporganizer.ApiClients.InternshipClient;
import com.internshiporganizer.Entities.EmployeeCheckable;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.Entities.Office;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InternshipAddEmployeesActivity extends AppCompatActivity implements Updatable<List<Internship>> {
    private EmployeeCheckableAdapter adapter;
    private ArrayList<EmployeeCheckable> employees;
    private Internship newInternship;
    private InternshipClient internshipClient;


    private Button buttonAdd;
    private ListView employeesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_add_employees);

        getSupportActionBar().setTitle("New internship");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        internshipClient = new InternshipClient(getApplicationContext(),this);
        buttonAdd = findViewById(R.id.internshipAddEmployees_buttonAdd);
        employeesListView = findViewById(R.id.internshipAddEmployees_listView);
        employees = new ArrayList<>();
        adapter = new EmployeeCheckableAdapter(InternshipAddEmployeesActivity.this, employees);
        employeesListView.setAdapter(adapter);

        getInternshipData();
        loadEmployees();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internshipClient.create(newInternship);
                for (EmployeeCheckable employee: employees) {
                    if(employee.getChecked()){

                    }
                }
            }
        });
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

    private void getInternshipData() {
        Intent intent = getIntent();

        newInternship = new Internship();
        newInternship.setTitle(intent.getStringExtra("title"));
        newInternship.setDescription(intent.getStringExtra("description"));
        newInternship.setCity(intent.getStringExtra("city"));


        newInternship.setStartDate(intent.getStringExtra("startDate"));
        newInternship.setFinishDate(intent.getStringExtra("endDate"));
    }

    private void loadEmployees() {
        Office office = new Office();
        office.setName("Arcus");
        EmployeeCheckable employeeCheckable = new EmployeeCheckable();
        employeeCheckable.setCity("Ufa");
        employeeCheckable.setOffice(office);
        employeeCheckable.setFirstName("Ivan");
        employeeCheckable.setLastName("Ivanov");
        employees.add(employeeCheckable);
        employees.add(employeeCheckable);
        employees.add(employeeCheckable);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void update(List<Internship> internships) {
        Toast.makeText(getApplicationContext(), "Internship created!", Toast.LENGTH_SHORT).show();
    }
}
