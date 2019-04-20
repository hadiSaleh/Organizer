package com.internshiporganizer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.internshiporganizer.ApiClients.EmployeeClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Employee;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import static com.internshiporganizer.Constants.RESULT_LOAD_IMAGE;

public class ProfileActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private StorageReference storageRef;
    private EmployeeClient employeeClient;
    private Employee employee;

    private long employeeId;
    private ImageView photoIV;
    private EditText firstNameET;
    private EditText secondNameET;
    private EditText cityET;
    private Button changeButton;

    private Uri imageUri;
    private Bitmap selectedImage;
    private boolean isPhotoChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storageRef = FirebaseStorage.getInstance().getReference();
        sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        employeeId = sharedPreferences.getLong(Constants.ID, -1);
        isPhotoChanged = false;

        employeeClient = new EmployeeClient(this, new Updatable<List<Employee>>() {
            @Override
            public void update(List<Employee> employees) {
                employee = employees.get(0);
                loadInfo();
            }
        });
        employeeClient.getOne(employeeId);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Profile");

        setViews();
    }

    private void setViews() {
        photoIV = findViewById(R.id.profile_photo);
        firstNameET = findViewById(R.id.profile_firstName);
        secondNameET = findViewById(R.id.profile_secondName);
        cityET = findViewById(R.id.profile_city);
        changeButton = findViewById(R.id.profile_change);

        photoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameET.getText().toString();
                String secondName = secondNameET.getText().toString();
                String city = cityET.getText().toString();

                if (firstName.equals("")) {
                    Toast.makeText(ProfileActivity.this, "First name must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (secondName.equals("")) {
                    Toast.makeText(ProfileActivity.this, "Second name must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (city.equals("")) {
                    Toast.makeText(ProfileActivity.this, "City must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInfoChanged = !(firstName.equals(employee.getFirstName())
                        && secondName.equals(employee.getLastName())
                        && city.equals(employee.getEmail()));

                if (!isInfoChanged && !isPhotoChanged) {
                    Toast.makeText(ProfileActivity.this, "No changes", Toast.LENGTH_SHORT).show();
                    finish();
                }

                if (isInfoChanged) {
                    EmployeeClient client = new EmployeeClient(ProfileActivity.this, new Updatable<List<Employee>>() {
                        @Override
                        public void update(List<Employee> employees) {
                            if (isPhotoChanged) {
                                updatePhoto();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                ProfileActivity.this.finish();
                            }
                        }
                    });

                    employee.setFirstName(firstName);
                    employee.setLastName(secondName);
                    employee.setCity(city);
                    client.update(employee);
                } else if (isPhotoChanged) {
                    updatePhoto();
                }
            }
        });

    }

    private void loadInfo() {
        firstNameET.setText(employee.getFirstName());
        secondNameET.setText(employee.getLastName());
        cityET.setText(employee.getCity());

        StorageReference riversRef = storageRef.child("images/employees/" + employeeId + "/image.jpg");
        File localFile = null;
        try {
            localFile = File.createTempFile("profileImage", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File finalLocalFile = localFile;
        riversRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        if (ProfileActivity.this == null) {
                            return;
                        }

                        photoIV.setImageURI(Uri.fromFile(finalLocalFile));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    private void updatePhoto() {
        if (imageUri == null) {
            return;
        }

        StorageReference riversRef = storageRef.child("images/employees/" + employeeId + "/image.jpg");
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        ProfileActivity.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ProfileActivity.this, "Cannot update image", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                photoIV.setImageBitmap(selectedImage);
                isPhotoChanged = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ProfileActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}
