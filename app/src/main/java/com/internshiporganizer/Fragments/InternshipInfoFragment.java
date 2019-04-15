package com.internshiporganizer.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.internshiporganizer.ApiClients.InternshipClient;
import com.internshiporganizer.ApiClients.InternshipImagesClient;
import com.internshiporganizer.Constants;
import com.internshiporganizer.Entities.Internship;
import com.internshiporganizer.R;
import com.internshiporganizer.Updatable;
import com.internshiporganizer.activities.FullScreenImageActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.internshiporganizer.Constants.RESULT_LOAD_IMAGE;

public class InternshipInfoFragment extends Fragment {
    private long internshipId;
    private InternshipClient internshipClient;
    private StorageReference storageRef;
    private SharedPreferences sharedPreferences;
    private InternshipImagesClient internshipImagesClient;

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

    private Bitmap selectedImage;
    private Uri imageUri;
    private int imageCount;
    boolean isImageFitToScreen;

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
        internshipId = getArguments().getLong(Constants.ID);
        storageRef = FirebaseStorage.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        setViews();
        setAddPhotoVisibility();

        internshipImagesClient = new InternshipImagesClient(getContext());
        loadInternship();
    }

    private void loadInternship() {
        internshipClient = new InternshipClient(getContext(), new Updatable<List<Internship>>() {
            @Override
            public void update(List<Internship> internships) {
                Internship internship = internships.get(0);
                imageCount = internship.getImageCount();

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

                loadPhotos();
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

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
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
    }

    private void setAddPhotoVisibility() {
        boolean administrator = sharedPreferences.getBoolean(Constants.IS_ADMINISTRATOR, false);
        if (!administrator) {
            addPhoto.setVisibility(View.GONE);
        }
    }

    private void loadPhotos() {
        for (int i = 0; i < imageCount; i++) {
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
                            photosLL.addView(insertPhoto(finalLocalFile));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(), "Cannot load photo", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private float toPixels(long dp) {
        Resources r = getActivity().getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
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

//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

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
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
//                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
//                selectedImage = BitmapFactory.decodeStream(imageStream);
//                picture.setImageBitmap(selectedImage);
                StorageReference riversRef = storageRef
                        .child("images/internships/" + internshipId + "/" + imageCount + ".jpg");

                riversRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                Toast.makeText(getContext(), downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                                imageCount++;
                                internshipImagesClient.updateImageCount(internshipId, imageCount);
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
        } else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}
