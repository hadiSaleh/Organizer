package com.internshiporganizer;

import android.accessibilityservice.GestureDescription;
import android.content.Intent;
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

    private AuthCredentialsClient authCredentialsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        authCredentialsClient = new AuthCredentialsClient(getApplicationContext(), this);
        enterButton = findViewById(R.id.activityLogin_button);
        activityLogin_editTextMail = findViewById(R.id.activityLogin_editTextMail);
        activityLogin_editTextPassword = findViewById(R.id.activityLogin_editTextPassword);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToLogin();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void tryToLogin() {
        String email = activityLogin_editTextMail.getText().toString();
        String password = activityLogin_editTextPassword.getText().toString();

        email = "test@test.test";
        password = "123";

        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setEmail(email);
        String hash = new String(Hex.encodeHex(DigestUtils.sha256(password)));
        authCredentials.setPasswordHash(hash.toUpperCase());

        authCredentialsClient.tryToLogin(authCredentials);
    }

    @Override
    public void update(Employee employee) {
        Toast.makeText(getApplicationContext(), "NAIS", Toast.LENGTH_SHORT).show();
    }
}
