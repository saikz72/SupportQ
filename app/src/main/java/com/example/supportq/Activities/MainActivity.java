package com.example.supportq.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.supportq.Fragments.ComposeFragment;
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
                    case R.id.action_compose:
                        //TODO: update fragment
                        fragment = new ComposeFragment();
                        Toast.makeText(MainActivity.this, "compose", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_home:
                        //TODO: update fragment
                        fragment = new HomeFragment();
                        Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_profile:
                        //TODO: update fragment
                        fragment = new ProfileFragment();
                        Toast.makeText(MainActivity.this, "profile", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_inbox:
                        //TODO: update fragment
                        fragment = new InboxFragment();
                        Toast.makeText(MainActivity.this, "mail", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        fragment = new HomeFragment();
                        Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT);

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