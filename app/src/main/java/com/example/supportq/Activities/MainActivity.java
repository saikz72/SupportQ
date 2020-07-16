package com.example.supportq.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.supportq.Fragments.HomeFragment;
import com.example.supportq.Fragments.InboxFragment;
import com.example.supportq.Fragments.ProfileFragment;
import com.example.supportq.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setViews();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        //TODO: update fragment
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_profile:
                        //TODO: update fragment
                        fragment = new ProfileFragment();
                        break;
                    case R.id.action_inbox:
                        //TODO: update fragment
                        fragment = new InboxFragment();
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
    }
}