package com.internshiporganizer.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.internshiporganizer.Adapters.GoalAttachmentAdapter;
import com.internshiporganizer.ApiClients.GoalAttachmentClient;
import com.internshiporganizer.ApiClients.GoalClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Goal;
import com.internshiporganizer.Entities.GoalAttachment;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.internshiporganizer.Constants.RESULT_LOAD_ATTACHMENT;

public class GoalActivity extends AppCompatActivity implements Updatable<List<Goal>> {
    private GoalAttachmentAdapter adapter;
    private ArrayList<GoalAttachment> attachments;
    private GoalAttachmentClient goalAttachmentClient;
    private Goal goal;

    private long goalId;
    private boolean isCompleted;
    private String internshipTitle;
    private GoalClient goalClient;

    private TextView titleTV;
    private TextView descriptionTV;
    private TextView placeTV;
    private TextView deadlineTV;
    private EditText noteET;
    private TextView addAttachmentTV;
    private Button completeButton;
    private ListView attachmentsLV;

    private SharedPreferences sharedPreferences;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        storageRef = FirebaseStorage.getInstance().getReference();

        goalId = getIntent().getLongExtra("goalId", -1);
        internshipTitle = getIntent().getStringExtra("internshipTitle");
        isCompleted = getIntent().getBooleanExtra("isCompleted", false);
        goalClient = new GoalClient(getApplicationContext(), this);

        titleTV = findViewById(R.id.goalActivity_title);
        descriptionTV = findViewById(R.id.goalActivity_description);
        placeTV = findViewById(R.id.goalActivity_place);
        deadlineTV = findViewById(R.id.goalActivity_deadline);
        noteET = findViewById(R.id.goalActivity_note);
        addAttachmentTV = findViewById(R.id.goalActivity_addAttachment);
        completeButton = findViewById(R.id.goalActivity_complete);

        attachmentsLV = findViewById(R.id.goalActivity_attachments);
        attachmentsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final GoalAttachment attachment = (GoalAttachment) adapter.getItem(position);
                DownloadManager downloadmanager = (DownloadManager) GoalActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(attachment.getUrl()));
                request.setTitle(attachment.getName());
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, attachment.getName());

                downloadmanager.enqueue(request);
            }
        });

        goalAttachmentClient = new GoalAttachmentClient(this, new Updatable<List<GoalAttachment>>() {
            @Override
            public void update(List<GoalAttachment> goalAttachments) {
                attachments.addAll(goalAttachments);
                adapter.notifyDataSetChanged();
            }
        });

        addAttachmentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("*/*");
                startActivityForResult(i, RESULT_LOAD_ATTACHMENT);
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = noteET.getText().toString();
                if (note.equals("")) {
                    Toast.makeText(GoalActivity.this, "Note must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                GoalClient client = new GoalClient(GoalActivity.this, new Updatable<List<Goal>>() {
                    @Override
                    public void update(List<Goal> goals) {
                        completeButton.setVisibility(View.GONE);
                        addAttachmentTV.setVisibility(View.GONE);

                        noteET.setFocusable(false);
                        noteET.setCursorVisible(false);
                        noteET.setKeyListener(null);
                        noteET.setBackgroundColor(Color.TRANSPARENT);
                        Toast.makeText(GoalActivity.this, "Goal updated", Toast.LENGTH_SHORT).show();
                    }
                });
                goal.setNote(note);
                goal.setCompleted(true);
                client.completeGoal(goal);
            }
        });

        setViewVisibility();

        loadGoal();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(internshipTitle);
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
    public void update(List<Goal> items) {
        Goal goal = items.get(0);
        this.goal = goal;

        titleTV.setText(goal.getTitle());
        descriptionTV.setText(goal.getDescription());
        placeTV.setText(goal.getPlace());
        deadlineTV.setText(goal.getDeadline().substring(0, 10));

        if (goal.getNote() != null) {
            noteET.setText(goal.getNote());
        }

        if (goal.getCompleted()) {
            completeButton.setVisibility(View.GONE);
            addAttachmentTV.setVisibility(View.GONE);

            noteET.setFocusable(false);
            noteET.setCursorVisible(false);
            noteET.setKeyListener(null);
            noteET.setBackgroundColor(Color.TRANSPARENT);
        }

        loadAttachments();
    }

    private void loadAttachments() {
        attachments = new ArrayList<>();
        adapter = new GoalAttachmentAdapter(this, attachments);
        attachmentsLV.setAdapter(adapter);

        goalAttachmentClient.getAllByGoal(goalId);
    }

    private void setViewVisibility() {
        if (isCompleted) {
            completeButton.setVisibility(View.GONE);
            addAttachmentTV.setVisibility(View.GONE);

            noteET.setFocusable(false);
            noteET.setCursorVisible(false);
            noteET.setKeyListener(null);
            noteET.setBackgroundColor(Color.TRANSPARENT);
            return;
        }

        boolean administrator = sharedPreferences.getBoolean(Constants.IS_ADMINISTRATOR, false);
        if (administrator || isCompleted) {
            findViewById(R.id.cardView6).setVisibility(View.GONE);
            findViewById(R.id.cardView7).setVisibility(View.GONE);
            completeButton.setVisibility(View.GONE);
        }
    }

    private void loadGoal() {
        goalClient.getById(goalId);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == RESULT_LOAD_ATTACHMENT) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "You haven't picked file", Toast.LENGTH_LONG).show();
                return;
            }

            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;
            if (uriString.startsWith("content://")) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            final GoalAttachment goalAttachment = new GoalAttachment();
            goalAttachment.setGoal(goal);
            goalAttachment.setName(displayName);
            final GoalAttachmentClient client = new GoalAttachmentClient(this, new Updatable<List<GoalAttachment>>() {
                @Override
                public void update(List<GoalAttachment> goalAttachments) {
                    loadAttachments();
                }
            });

            final StorageReference riversRef = storageRef.child("attachments/goals/" + goalId + "/" + displayName);
            riversRef.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        goalAttachment.setUrl(downloadUri.toString());
                        client.add(goalAttachment);
                    } else {
                        Toast.makeText(GoalActivity.this, "Cannot add attachment", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Toast.makeText(this, displayName, Toast.LENGTH_LONG).show();
        }
    }
}
