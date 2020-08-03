package com.example.supportq.Activities.Instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.supportq.Fragments.HomeFragment;
import com.example.supportq.Fragments.Instructor.EventFragment;
import com.example.supportq.Fragments.ProfileFragment;
import com.example.supportq.Fragments.Student.InboxFragment;
import com.example.supportq.Models.ProgressIndicator;
import com.example.supportq.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InstructorMainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    public final FragmentManager fragmentManager = getSupportFragmentManager();
    private Toolbar toolbar;
    public static View snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_main);
        ProgressIndicator.hideMessage(this);
        setViews();
        setSupportActionBar(toolbar);
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

    public void setViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        toolbar = findViewById(R.id.toolbar);
        snackbar = findViewById(R.id.snackbar);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}