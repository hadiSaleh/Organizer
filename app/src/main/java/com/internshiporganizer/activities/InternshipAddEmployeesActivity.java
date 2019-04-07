package com.internshiporganizer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.internshiporganizer.Adapters.EmployeeCheckableAdapter;
import com.internshiporganizer.ApiClients.EmployeeClient;
import com.internshiporganizer.ApiClients.InternshipCreationClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.Entities.EmployeeCheckable;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;

import java.util.ArrayList;
import java.util.List;

public class InternshipAddEmployeesActivity extends AppCompatActivity {
    private EmployeeCheckableAdapter adapter;
    private ArrayList<EmployeeCheckable> employeesCheckable;
    private Internship newInternship;
    private EmployeeClient employeeClient;
    private InternshipCreationClient internshipCreationClient;
    private SharedPreferences sharedPreferences;

    private Button buttonAdd;
    private ListView employeesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_add_employees);

        getSupportActionBar().setTitle("New internship");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        internshipCreationClient = new InternshipCreationClient(getApplicationContext(), new Updatable<Internship>() {
            @Override
            public void update(Internship internship) {
                Toast.makeText(getApplicationContext(), "Internship created!", Toast.LENGTH_SHORT).show();
            }
        });

        sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        buttonAdd = findViewById(R.id.internshipAddEmployees_buttonAdd);
        employeesListView = findViewById(R.id.internshipAddEmployees_listView);
        employeesCheckable = new ArrayList<>();
        adapter = new EmployeeCheckableAdapter(InternshipAddEmployeesActivity.this, employeesCheckable);
        employeesListView.setAdapter(adapter);

        getInternshipData();
        loadEmployees();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Employee> checkedEmployees = new ArrayList<>();
                for (EmployeeCheckable employee : employeesCheckable) {
                    if (employee.getChecked()) {
                        checkedEmployees.add(employee);
                    }
                }
                Employee administrator = new Employee();
                administrator.setId(sharedPreferences.getLong(Constants.ID,0));

                internshipCreationClient.create(newInternship, administrator, checkedEmployees);
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
        newInternship.setEndDate(intent.getStringExtra("endDate"));
    }

    private void loadEmployees() {
        employeeClient = new EmployeeClient(getApplicationContext(), new Updatable<List<Employee>>() {
            @Override
            public void update(List<Employee> employees) {
                for (Employee employee : employees) {
                    EmployeeCheckable employeeCheckable = new EmployeeCheckable(employee);
                    employeesCheckable.add(employeeCheckable);
                }

                adapter.notifyDataSetChanged();
            }
        });

        employeeClient.get();

//        Office office = new Office();
//        office.setName("Arcus");
//        EmployeeCheckable employeeCheckable = new EmployeeCheckable();
//        employeeCheckable.setCity("Ufa");
//        employeeCheckable.setOffice(office);
//        employeeCheckable.setFirstName("Ivan");
//        employeeCheckable.setLastName("Ivanov");
//        employeesCheckable.add(employeeCheckable);
//        employeesCheckable.add(employeeCheckable);
//        employeesCheckable.add(employeeCheckable);
//        adapter.notifyDataSetChanged();
    }
}
