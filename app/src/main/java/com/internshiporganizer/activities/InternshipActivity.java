package com.internshiporganizer.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.internshiporganizer.Adapters.ViewPagerAdapter;
import com.internshiporganizer.Fragments.ChatFragment;
import com.internshiporganizer.Fragments.GoalsFragment;
import com.internshiporganizer.Fragments.InternshipInfoFragment;
import com.internshiporganizer.R;

public class InternshipActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship);
//        setResult(RESULT_OK, null);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Internship title");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        InternshipInfoFragment internshipInfoFragment = new InternshipInfoFragment();
        adapter.addFragment(internshipInfoFragment, "Info");

        GoalsFragment goalsFragment = new GoalsFragment();
        adapter.addFragment(goalsFragment, "Goals");

        ChatFragment chatFragment = new ChatFragment();
        adapter.addFragment(chatFragment, "Chat");

        viewPager.setAdapter(adapter);
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