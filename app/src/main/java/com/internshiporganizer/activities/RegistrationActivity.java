package com.internshiporganizer.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.internshiporganizer.ApiClients.EmployeeClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.Entities.NewEmployee;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private EditText firstNameET;
    private EditText secondNameET;
    private EditText emailET;
    private EditText cityET;
    private EditText passwordET;
    private EditText confirmPasswordET;
    private Button completeButton;
    private CheckBox requestAdminCB;

    private SharedPreferences sharedPreferences;
    private EmployeeClient employeeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);

        getSupportActionBar().setTitle(R.string.registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        employeeClient = new EmployeeClient(this, new Updatable<List<Employee>>() {
            @Override
            public void update(List<Employee> employees) {
                Toast.makeText(RegistrationActivity.this, "Registration is complete", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        setViews();
    }

    private void setViews() {
        firstNameET = findViewById(R.id.registration_firstName);
        secondNameET = findViewById(R.id.registration_lastName);
        emailET = findViewById(R.id.registration_email);
        cityET = findViewById(R.id.registration_city);
        passwordET = findViewById(R.id.registration_password);
        confirmPasswordET = findViewById(R.id.registration_confirmPassword);
        completeButton = findViewById(R.id.registration_buttonFinish);
        requestAdminCB = findViewById(R.id.registration_requestAdmin);

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameET.getText().toString();
                String secondName = secondNameET.getText().toString();
                String email = emailET.getText().toString();
                String city = cityET.getText().toString();
                String password = passwordET.getText().toString();
                String confirmPassword = confirmPasswordET.getText().toString();
                boolean requestAdmin = requestAdminCB.isChecked();

                if (firstName.equals("")) {
                    Toast.makeText(RegistrationActivity.this, "First name must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (secondName.equals("")) {
                    Toast.makeText(RegistrationActivity.this, "Second name must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Pattern.matches("(.+)@(.+)\\.(.+)", email) || email.contains(" ")) {
                    Toast.makeText(RegistrationActivity.this, "Incorrect email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (city.equals("")) {
                    Toast.makeText(RegistrationActivity.this, "City must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(RegistrationActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                NewEmployee employee = new NewEmployee();
                employee.setFirstName(firstName);
                employee.setLastName(secondName);
                employee.setEmail(email);
                employee.setCity(city);
                employee.setAdministrator(requestAdmin);
                employee.setPasswordHash(new String(Hex.encodeHex(DigestUtils.sha256(password))));

                if (sharedPreferences.contains(Constants.FIREBASE_TOKEN)) {
                    employee.setFireBaseToken(sharedPreferences.getString(Constants.FIREBASE_TOKEN, ""));
                }

                employeeClient.createEmployee(employee);
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
}
