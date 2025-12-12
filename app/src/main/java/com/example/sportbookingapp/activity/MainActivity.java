package com.example.sportbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.fragment.ExploreFragment;
import com.example.sportbookingapp.fragment.HistoryFragment;
import com.example.sportbookingapp.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Mặc định load màn hình Khám phá nếu không có trạng thái lưu trước đó
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_explore) {
                selectedFragment = new ExploreFragment();
            } else if (id == R.id.nav_booking) {
                selectedFragment = new HistoryFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("open_tab")) {
            int tabIndex = intent.getIntExtra("open_tab", 0);
            if (tabIndex == 1) {
                bottomNavigationView.setSelectedItemId(R.id.nav_booking);
                loadFragment(new HistoryFragment());
            } else {
                bottomNavigationView.setSelectedItemId(R.id.nav_explore);
                loadFragment(new ExploreFragment());
            }
        } else {
            // Mặc định mở tab đầu tiên
            loadFragment(new ExploreFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}