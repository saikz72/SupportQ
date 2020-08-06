package com.example.supportq.Activities.Instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.supportq.Fragments.HomeFragment;
import com.example.supportq.Fragments.Instructor.EventFragment;
import com.example.supportq.Fragments.ProfileFragment;
import com.example.supportq.Fragments.InboxFragment;
import com.example.supportq.Models.Event;
import com.example.supportq.Models.ProgressIndicator;
import com.example.supportq.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class InstructorMainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNavigationView;
    public final FragmentManager fragmentManager = getSupportFragmentManager();
    private Toolbar toolbar;
    private BadgeDrawable badgeDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_main);
        ProgressIndicator.hideMessage(this);
        setViews();
        queryEvents();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.action_inbox:
                        fragment = new InboxFragment();
                        break;
                    case R.id.action_compose:
                        fragment = new EventFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    private void setViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        toolbar = findViewById(R.id.toolbar);
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.action_inbox);
        badgeDrawable.setVisible(true);
        setSupportActionBar(toolbar);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void queryEvents() {
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include(Event.KEY_USER);
        query.addDescendingOrder(Event.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    return;
                }
                badgeDrawable.setNumber(events.size());
            }
        });
    }
}