package com.internshiporganizer.Fragments;


import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.internshiporganizer.Adapters.InternshipAttachmentAdapter;
import com.internshiporganizer.ApiClients.InternshipAttachmentClient;
import com.internshiporganizer.ApiClients.InternshipClient;
import com.internshiporganizer.ApiClients.InternshipImageClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.Entities.InternshipAttachment;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.activities.FullScreenImageActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.internshiporganizer.Constants.RESULT_LOAD_ATTACHMENT;
import static com.internshiporganizer.Constants.RESULT_LOAD_IMAGE;

public class InternshipInfoFragment extends Fragment {
    private InternshipAttachmentAdapter adapter;
    private ArrayList<InternshipAttachment> attachments;

    private long internshipId;
    private InternshipClient internshipClient;
    private InternshipImageClient internshipImageClient;
    private InternshipAttachmentClient internshipAttachmentClient;
    private StorageReference storageRef;
    private SharedPreferences sharedPreferences;
    private DisplayMetrics displayMetrics;

    private TextView titleTV;
    private TextView descriptionTV;
    private TextView startDateTV;
    private TextView endDateTV;
    private TextView cityTV;
    private TextView addressTV;
    private TextView organizationTV;
    private TextView emailTV;
    private TextView phoneTV;
    private ImageView addPhoto;
    private LinearLayout photosLL;
    private TextView addAttachmentTV;
    private ListView attachmentLV;
    private Button completeButton;

    private Bitmap selectedImage;
    private int attachmentCount;
    private int imageCount;
    private boolean isImageFitToScreen;
    private boolean isCompleted;

    public InternshipInfoFragment() {
    }

    public static InternshipInfoFragment newInstance(long internshipId) {
        InternshipInfoFragment f = new InternshipInfoFragment();
        Bundle bdl = new Bundle(2);
        bdl.putLong(Constants.ID, internshipId);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        displayMetrics = getActivity().getResources().getDisplayMetrics();

        internshipId = getArguments().getLong(Constants.ID);
        storageRef = FirebaseStorage.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        setViews();

        internshipImageClient = new InternshipImageClient(getContext());
        internshipAttachmentClient = new InternshipAttachmentClient(getContext(), new Updatable<List<InternshipAttachment>>() {
            @Override
            public void update(List<InternshipAttachment> internshipAttachments) {
                attachments.addAll(internshipAttachments);
                adapter.notifyDataSetChanged();
            }
        });

        loadInternship();
    }

    private void loadInternship() {
        internshipClient = new InternshipClient(getContext(), new Updatable<List<Internship>>() {
            @Override
            public void update(List<Internship> internships) {
                Internship internship = internships.get(0);
                imageCount = internship.getImageCount();
                isCompleted = !internship.getActive();
                setViewsVisibility();

                titleTV.setText(internship.getTitle());
                descriptionTV.setText(internship.getDescription());
                cityTV.setText(internship.getCity());
                startDateTV.setText(internship.getStartDate().substring(0, 10));
                endDateTV.setText(internship.getEndDate().substring(0, 10));
                addressTV.setText(internship.getAddress());

                if (internship.getOrganization() == null) {
                    organizationTV.setVisibility(View.GONE);
                    getView().findViewById(R.id.textView8).setVisibility(View.GONE);
                } else {
                    organizationTV.setText(internship.getOrganization());
                }

                if (internship.getEmail() == null) {
                    emailTV.setVisibility(View.GONE);
                    getView().findViewById(R.id.textView10).setVisibility(View.GONE);
                } else {
                    emailTV.setText(internship.getEmail());
                }

                if (internship.getPhoneNumber() == null) {
                    phoneTV.setVisibility(View.GONE);
                    getView().findViewById(R.id.textView11).setVisibility(View.GONE);
                } else {
                    phoneTV.setText(internship.getPhoneNumber());
                }

                try {
                    loadPhotos();
                    loadAttachments();
                } catch (Exception ignored) {
                }
            }
        });

        internshipClient.getById(internshipId);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViews() {
        titleTV = getView().findViewById(R.id.currentInternship_textViewTitle);
        descriptionTV = getView().findViewById(R.id.currentInternship_textViewDescription);
        startDateTV = getView().findViewById(R.id.currentInternship_textViewStartDate);
        endDateTV = getView().findViewById(R.id.currentInternship_textViewEndDate);
        cityTV = getView().findViewById(R.id.currentInternship_textViewCity);
        addressTV = getView().findViewById(R.id.currentInternship_textViewAddress);
        organizationTV = getView().findViewById(R.id.currentInternship_textViewOrganization);
        emailTV = getView().findViewById(R.id.currentInternship_textViewEmail);
        phoneTV = getView().findViewById(R.id.currentInternship_textViewPhone);
        addPhoto = getView().findViewById(R.id.currentInternship_addPhoto);
        photosLL = getView().findViewById(R.id.currentInternship_photos);
        addAttachmentTV = getView().findViewById(R.id.currentInternship_addAttachment);
        completeButton = getView().findViewById(R.id.currentInternship_buttonComplete);
        attachmentLV = getView().findViewById(R.id.currentInternship_attachments);
        attachmentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final InternshipAttachment attachment = (InternshipAttachment) adapter.getItem(position);
                DownloadManager downloadmanager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(attachment.getUrl()));
                request.setTitle(attachment.getName());
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, attachment.getName());

                downloadmanager.enqueue(request);
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
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

        addPhoto.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }

                return false;
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InternshipClient client = new InternshipClient(getContext(), new Updatable<List<Internship>>() {
                    @Override
                    public void update(List<Internship> internships) {
                        Toast.makeText(getContext(), "Internship completed!", Toast.LENGTH_LONG).show();

                        isCompleted = true;
                        setViewsVisibility();
                    }
                });

                Internship internship = new Internship();
                internship.setId(internshipId);
                internship.setActive(false);
                client.complete(internship);
            }
        });
    }

    private void setViewsVisibility() {
        boolean administrator = sharedPreferences.getBoolean(Constants.IS_ADMINISTRATOR, false);
        if (!administrator || isCompleted) {
            addPhoto.setVisibility(View.GONE);
            addAttachmentTV.setVisibility(View.GONE);
            completeButton.setVisibility(View.GONE);
        }
    }

    private void loadAttachments() {
        attachments = new ArrayList<>();
        adapter = new InternshipAttachmentAdapter(getContext(), attachments);
        attachmentLV.setAdapter(adapter);

        internshipAttachmentClient.getAllByInternship(internshipId);
    }

    private void loadPhotos() {
        for (int i = 0; i < imageCount; i++) {
            loadPhoto(i);
        }
    }

    private void loadPhoto(int i) {
        StorageReference riversRef = storageRef.child("images/internships/" + internshipId + "/" + i + ".jpg");
        File localFile = null;
        try {
            localFile = File.createTempFile("img" + String.valueOf(i), "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File finalLocalFile = localFile;
        riversRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        if (getContext() == null) {
                            return;
                        }

                        photosLL.addView(insertPhoto(finalLocalFile));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (getContext() == null) {
                    return;
                }

                Toast.makeText(getContext(), "Cannot load photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float toPixels(long dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private View insertPhoto(final File file) {
        Bitmap bm = decodeSampledBitmapFromUri(file.getAbsolutePath(), toPixels(90), toPixels(90));

        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new LayoutParams(Math.round(toPixels(96)), Math.round(toPixels(96))));
        layout.setGravity(Gravity.CENTER);

        final ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(Math.round(toPixels(90)), Math.round(toPixels(90))));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bm);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullScreenIntent = new Intent(getContext(), FullScreenImageActivity.class);
                fullScreenIntent.setData(Uri.fromFile(file));
                fullScreenIntent.putExtra("internshipTitle", titleTV.getText().toString());
                startActivity(fullScreenIntent);
            }
        });

        layout.addView(imageView);
        return layout;
    }

    private Bitmap decodeSampledBitmapFromUri(String path, float reqWidth, float reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / reqHeight);
            } else {
                inSampleSize = Math.round((float) width / reqWidth);
            }
        }

        return inSampleSize;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_internship_info, container, false);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == RESULT_LOAD_IMAGE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                Uri imageUri = data.getData();
                StorageReference riversRef = storageRef
                        .child("images/internships/" + internshipId + "/" + imageCount + ".jpg");

                riversRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                imageCount++;
                                internshipImageClient.updateImageCount(internshipId, imageCount);
                                loadPhoto(imageCount - 1);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getContext(), "Cannot add image", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }

        if (reqCode == RESULT_LOAD_ATTACHMENT) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(getActivity(), "You haven't picked file", Toast.LENGTH_LONG).show();
                return;
            }

            Uri uri = data.getData();
            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;
            if (uriString.startsWith("content://")) {
                try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            Internship internship = new Internship();
            internship.setId(internshipId);
            final InternshipAttachment internshipAttachment = new InternshipAttachment();
            internshipAttachment.setInternship(internship);
            internshipAttachment.setName(displayName);
            final InternshipAttachmentClient client = new InternshipAttachmentClient(getContext(), new Updatable<List<InternshipAttachment>>() {
                @Override
                public void update(List<InternshipAttachment> internshipAttachments) {
                    loadAttachments();
                }
            });

            final StorageReference riversRef = storageRef.child("attachments/internships/" + internshipId + "/" + displayName);
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
                        internshipAttachment.setUrl(downloadUri.toString());
                        client.add(internshipAttachment);
                    } else {
                        Toast.makeText(getContext(), "Cannot add attachment", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Toast.makeText(getActivity(), displayName, Toast.LENGTH_LONG).show();
        }
    }
}
