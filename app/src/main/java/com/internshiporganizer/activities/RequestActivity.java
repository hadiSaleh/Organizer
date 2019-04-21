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
import com.internshiporganizer.Adapters.RequestAttachmentAdapter;
import com.internshiporganizer.ApiClients.RequestAttachmentClient;
import com.internshiporganizer.ApiClients.RequestClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Request;
import com.internshiporganizer.Entities.RequestAttachment;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.internshiporganizer.Constants.RESULT_LOAD_ATTACHMENT;

public class RequestActivity extends AppCompatActivity implements Updatable<List<Request>> {
    private RequestAttachmentAdapter adapter;
    private ArrayList<RequestAttachment> attachments;
    private RequestAttachmentClient requestAttachmentClient;
    private Request request;

    private long requestId;
    private boolean isCompleted;
    private String internshipTitle;
    private RequestClient requestClient;

    private TextView titleTV;
    private TextView descriptionTV;
    private EditText noteET;
    private TextView addAttachmentTV;
    private Button completeButton;
    private ListView attachmentsLV;

    private SharedPreferences sharedPreferences;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        storageRef = FirebaseStorage.getInstance().getReference();

        requestId = getIntent().getLongExtra("requestId", -1);
        internshipTitle = getIntent().getStringExtra("internshipTitle");
        isCompleted = getIntent().getBooleanExtra("isCompleted", false);
        requestClient = new RequestClient(getApplicationContext(), this);

        titleTV = findViewById(R.id.requestActivity_title);
        descriptionTV = findViewById(R.id.requestActivity_description);
        noteET = findViewById(R.id.requestActivity_note);
        addAttachmentTV = findViewById(R.id.requestActivity_addAttachment);
        completeButton = findViewById(R.id.requestActivity_complete);

        attachmentsLV = findViewById(R.id.requestActivity_attachments);
        attachmentsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final RequestAttachment attachment = (RequestAttachment) adapter.getItem(position);
                DownloadManager downloadmanager = (DownloadManager) RequestActivity.this.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(attachment.getUrl()));
                request.setTitle(attachment.getName());
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, attachment.getName());

                downloadmanager.enqueue(request);
            }
        });

        requestAttachmentClient = new RequestAttachmentClient(this, new Updatable<List<RequestAttachment>>() {
            @Override
            public void update(List<RequestAttachment> requestAttachments) {
                attachments.addAll(requestAttachments);
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
                    Toast.makeText(RequestActivity.this, "Note must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestClient client = new RequestClient(RequestActivity.this, new Updatable<List<Request>>() {
                    @Override
                    public void update(List<Request> goals) {
                        completeButton.setVisibility(View.GONE);
                        addAttachmentTV.setVisibility(View.GONE);

                        noteET.setFocusable(false);
                        noteET.setCursorVisible(false);
                        noteET.setKeyListener(null);
                        noteET.setBackgroundColor(Color.TRANSPARENT);
                        Toast.makeText(RequestActivity.this, "Request updated", Toast.LENGTH_SHORT).show();
                    }
                });
                request.setNote(note);
                request.setCompleted(true);
                client.completeRequest(request);
            }
        });

        setViewVisibility();

        loadRequest();

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
    public void update(List<Request> items) {
        Request request = items.get(0);
        this.request = request;

        titleTV.setText(request.getTitle());
        descriptionTV.setText(request.getDescription());

        if (request.getNote() != null) {
            noteET.setText(request.getNote());
        }

        if (request.getCompleted()) {
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
        adapter = new RequestAttachmentAdapter(this, attachments);
        attachmentsLV.setAdapter(adapter);

        requestAttachmentClient.getAllByRequest(requestId);
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
        if (administrator) {
            findViewById(R.id.cardView6).setVisibility(View.GONE);
            findViewById(R.id.cardView7).setVisibility(View.GONE);
            completeButton.setVisibility(View.GONE);
        }
    }

    private void loadRequest() {
        requestClient.getById(requestId);
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

            final RequestAttachment requestAttachment = new RequestAttachment();
            requestAttachment.setRequest(request);
            requestAttachment.setName(displayName);
            final RequestAttachmentClient client = new RequestAttachmentClient(this, new Updatable<List<RequestAttachment>>() {
                @Override
                public void update(List<RequestAttachment> requestAttachments) {
                    loadAttachments();
                }
            });

            final StorageReference riversRef = storageRef.child("attachments/requests/" + requestId + "/" + displayName);
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
                        requestAttachment.setUrl(downloadUri.toString());
                        client.add(requestAttachment);
                    } else {
                        Toast.makeText(RequestActivity.this, "Cannot add attachment", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Toast.makeText(this, displayName, Toast.LENGTH_LONG).show();
        }
    }
}
