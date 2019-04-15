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
import java.util.Calendar;
import java.util.Date;
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

                Intent intent = new Intent(InternshipAddEmployeesActivity.this, InternshipActivity.class);
                intent.putExtra("internshipTitle", internship.getTitle());
                intent.putExtra("internshipId", internship.getId());
                startActivity(intent);

                setResult(RESULT_OK, null);
                finish();
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
                administrator.setId(sharedPreferences.getLong(Constants.ID, 0));

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

        String startDate = intent.getStringExtra("startDate");

        newInternship = new Internship();
        newInternship.setTitle(intent.getStringExtra("title"));
        newInternship.setDescription(intent.getStringExtra("description"));
        newInternship.setCity(intent.getStringExtra("city"));
        newInternship.setAddress(intent.getStringExtra("address"));
        newInternship.setActive(isActive(startDate));
        newInternship.setStartDate(startDate);
        newInternship.setEndDate(intent.getStringExtra("endDate"));

        String organization = intent.getStringExtra("organization");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        if (!organization.equals("")) {
            newInternship.setOrganization(organization);
        }
        if (!email.equals("")) {
            newInternship.setEmail(email);
        }
        if (!phone.equals("")) {
            newInternship.setPhoneNumber(phone);
        }
    }

    private boolean isActive(String startDate) {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String[] date = startDate.split("-");

        return Integer.valueOf(date[0]) >= year
                && Integer.valueOf(date[1]) >= month
                && Integer.valueOf(date[2]) >= day;
    }

    private void loadEmployees() {
        final long id = sharedPreferences.getLong(Constants.ID, 0);

        employeeClient = new EmployeeClient(getApplicationContext(), new Updatable<List<Employee>>() {
            @Override
            public void update(List<Employee> employees) {
                for (Employee employee : employees) {
                    if (employee.getId() == id) {
                        continue;
                    }

                    EmployeeCheckable employeeCheckable = new EmployeeCheckable(employee);
                    employeesCheckable.add(employeeCheckable);
                }

                adapter.notifyDataSetChanged();
            }
        });

        employeeClient.get();
    }
}
