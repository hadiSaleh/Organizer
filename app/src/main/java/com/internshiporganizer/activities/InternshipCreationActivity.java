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

import com.internshiporganizer.Constants;
import com.internshiporganizer.R;

import java.util.Calendar;

public class InternshipCreationActivity extends AppCompatActivity {
    private EditText titleET;
    private EditText descriptionET;
    private EditText cityET;
    private TextView startDate;
    private TextView endDate;
    private EditText addressET;
    private EditText organizationET;
    private EditText emailET;
    private EditText phoneET;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_creation);

        titleET = findViewById(R.id.internshipCreationTitle);
        descriptionET = findViewById(R.id.internshipCreationDescription);
        cityET = findViewById(R.id.internshipCreationCity);
        startDate = findViewById(R.id.textViewStartDate);
        endDate = findViewById(R.id.textViewEndDate);
        addressET = findViewById(R.id.internshipCreation_address);
        organizationET = findViewById(R.id.internshipCreation_organization);
        emailET = findViewById(R.id.internshipCreation_email);
        phoneET = findViewById(R.id.internshipCreation_phone);

        nextButton = findViewById(R.id.internshipCreation_buttonNext);

        setListeners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("New internship");
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

    private void setListeners() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        startDate.setText(String.format("%d-%02d-%02d", year, month, day));
        endDate.setText(String.format("%d-%02d-%02d", year, month, day));

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(InternshipCreationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDate.setText(String.format("%d-%02d-%02d", year, month, dayOfMonth));
                    }
                }, year, month, day);
                dialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(InternshipCreationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate.setText(String.format("%d-%02d-%02d", year, month, dayOfMonth));
                    }
                }, year, month, day);
                dialog.show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleET.getText().toString();
                String description = descriptionET.getText().toString();
                String city = cityET.getText().toString();
                String address = addressET.getText().toString();

                if (title.equals("")) {
                    Toast.makeText(InternshipCreationActivity.this, "Title can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (description.equals("")) {
                    Toast.makeText(InternshipCreationActivity.this, "Description can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (address.equals("")) {
                    Toast.makeText(InternshipCreationActivity.this, "Address can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                String[] lines = description.split("\r\n|\r|\n");
                if (lines.length > 10) {
                    Toast.makeText(InternshipCreationActivity.this, "Description should not exceed 2 lines!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (city.equals("")) {
                    Toast.makeText(InternshipCreationActivity.this, "City can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(InternshipCreationActivity.this, InternshipAddEmployeesActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("city", city);
                intent.putExtra("address", address);
                intent.putExtra("organization", organizationET.getText().toString());
                intent.putExtra("email", emailET.getText().toString());
                intent.putExtra("phone", phoneET.getText().toString());
                intent.putExtra("startDate", startDate.getText().toString());
                intent.putExtra("endDate", endDate.getText().toString());
                startActivityForResult(intent, Constants.REQUEST_EXIT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                this.finish();

            }
        }
    }
}
