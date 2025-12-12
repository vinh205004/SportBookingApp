package com.example.sportbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.fragment.HistoryFragment;
import com.example.sportbookingapp.fragment.HomeFragment;
import com.example.sportbookingapp.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Cấu hình OSM
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        // 2. Mặc định hiển thị HomeFragment khi lần đầu mở App
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // 3. Bắt sự kiện bấm Menu đáy
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_explore) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_booking) {
                selectedFragment = new HistoryFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        // 4. Xử lý tín hiệu nếu được mở từ trang Đặt sân
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Cập nhật Intent mới nhất
        handleIntent(intent); // Xử lý yêu cầu chuyển tab
    }

    // Hàm xử lý logic chuyển tab tự động
    private void handleIntent(Intent intent) {
        String openFragment = intent.getStringExtra("OPEN_FRAGMENT");

        if (openFragment != null && openFragment.equals("History")) {
            // 1. Chuyển sang Fragment Lịch sử
            loadFragment(new HistoryFragment());

            // 2. Cập nhật icon sáng ở menu
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_booking);
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}