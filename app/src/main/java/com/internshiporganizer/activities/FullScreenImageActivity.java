package com.internshiporganizer.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.internshiporganizer.R;

public class FullScreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        ImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        Intent callingActivityIntent = getIntent();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(callingActivityIntent.getStringExtra("internshipTitle"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
//        getSupportActionBar().hide();

        if (callingActivityIntent != null) {
            Uri imageUri = callingActivityIntent.getData();
            if (imageUri != null && fullScreenImageView != null) {
                Glide.with(this)
                        .load(imageUri)
                        .into(fullScreenImageView);
            }
        }
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