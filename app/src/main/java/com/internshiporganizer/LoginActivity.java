package com.internshiporganizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.internshiporganizer.ApiClients.AuthCredentialsClient;
import com.internshiporganizer.Entities.AuthCredentials;
import com.internshiporganizer.Entities.Employee;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class LoginActivity extends AppCompatActivity implements Updatable<Employee> {
    private EditText activityLogin_editTextMail;
    private EditText activityLogin_editTextPassword;
    private Button enterButton;
    private SharedPreferences sharedPreferences;
    private String email;
    private String passwordHash;

    private AuthCredentialsClient authCredentialsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setResult(RESULT_OK, null);
        getSupportActionBar().hide();

        authCredentialsClient = new AuthCredentialsClient(getApplicationContext(), this);
        enterButton = findViewById(R.id.activityLogin_button);
        activityLogin_editTextMail = findViewById(R.id.activityLogin_editTextMail);
        activityLogin_editTextPassword = findViewById(R.id.activityLogin_editTextPassword);

        sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        checkPreferences();

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = activityLogin_editTextMail.getText().toString();
                String password = activityLogin_editTextPassword.getText().toString();
                passwordHash = new String(Hex.encodeHex(DigestUtils.sha256(password)));
                tryLogin();
            }
        });
    }

    @Override
    public void update(Employee employee) {
        Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
        sharedPreferences.edit()
                .putString(Constants.EMAIL, email)
                .putString(Constants.PASSWORD_HASH, passwordHash)
                .putBoolean(Constants.IS_ADMINISTRATOR, employee.getAdministrator())
                .putLong(Constants.ID, employee.getId()).apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivityForResult(intent, Constants.REQUEST_EXIT);
    }

    private void checkPreferences() {
        if (!sharedPreferences.contains(Constants.EMAIL)) {
            return;
        }
        if (!sharedPreferences.contains(Constants.PASSWORD_HASH)) {
            return;
        }
        if (!sharedPreferences.contains(Constants.IS_ADMINISTRATOR)) {
            return;
        }
        if (!sharedPreferences.contains(Constants.ID)) {
            return;
        }

        email = sharedPreferences.getString(Constants.EMAIL, "");
        passwordHash = sharedPreferences.getString(Constants.PASSWORD_HASH, "");
        tryLogin();
    }

    private void tryLogin() {
        email = "test0@test.test";
        passwordHash = "A665A45920422F9D417E4867EFDC4FB8A04A1F3FFF1FA07E998E86F7F7A27AE3";

        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setEmail(email);
        authCredentials.setPasswordHash(passwordHash.toUpperCase());

        authCredentialsClient.tryLogin(authCredentials);
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
