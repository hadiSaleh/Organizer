package com.internshiporganizer.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.internshiporganizer.Adapters.ViewPagerAdapter;
import com.internshiporganizer.Fragments.ChatFragment;
import com.internshiporganizer.Fragments.GoalEmployeesFragment;
import com.internshiporganizer.Fragments.GoalsFragment;
import com.internshiporganizer.Fragments.InternshipInfoFragment;
import com.internshiporganizer.Fragments.RequestsFragment;
import com.internshiporganizer.R;

public class InternshipActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String internshipTitle;
    private long internshipId;
    private boolean isCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship);
        internshipTitle = getIntent().getStringExtra("internshipTitle");
        internshipId = getIntent().getLongExtra("internshipId", -1);
        isCompleted = getIntent().getBooleanExtra("isCompleted", false);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(internshipTitle);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        InternshipInfoFragment internshipInfoFragment = InternshipInfoFragment.newInstance(internshipId);
        adapter.addFragment(internshipInfoFragment, "Info");

        if (isCompleted) {
            GoalEmployeesFragment goalEmployeesFragment = GoalEmployeesFragment.newInstance(internshipId);
            adapter.addFragment(goalEmployeesFragment, "Goals");
            goalEmployeesFragment.setInternshipTitle(internshipTitle);
        } else {
            RequestsFragment requestsFragment = RequestsFragment.newInstance(internshipId, isCompleted);
            adapter.addFragment(requestsFragment, "Requests");
            requestsFragment.setInternshipTitle(internshipTitle);

            GoalsFragment goalsFragment = GoalsFragment.newInstance(internshipId, isCompleted);
            adapter.addFragment(goalsFragment, "Goals");
            goalsFragment.setInternshipTitle(internshipTitle);
        }

        ChatFragment chatFragment = ChatFragment.newInstance(internshipId);
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
