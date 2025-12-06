/*
package com.example.sportbookingapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportbookingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvPhone, tvEmail;
    private ImageView btnBack, btnEditName, btnEditPhone, btnEditEmail;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ view
        btnBack = findViewById(R.id.btn_back);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);

        btnEditName = findViewById(R.id.btn_edit_name);
        btnEditPhone = findViewById(R.id.btn_edit_phone);
        btnEditEmail = findViewById(R.id.btn_edit_email);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set thông tin tĩnh
        tvName.setText("Nguyễn Văn A");
        tvPhone.setText("0123 456 789");
        tvEmail.setText("user@example.com");

        // Nút back quay về màn hình trước
        btnBack.setOnClickListener(v -> onBackPressed());

        // Các nút edit chỉ hiển thị icon (không thay đổi dữ liệu)
        btnEditName.setOnClickListener(v -> {
            // Có thể thêm Toast hoặc dialog thông báo "Chức năng sửa tạm chưa có"
        });
        btnEditPhone.setOnClickListener(v -> {
            // Tương tự
        });
        btnEditEmail.setOnClickListener(v -> {
            // Tương tự
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_explore) {
                // xử lý khám phá
                return true;
            } else if (id == R.id.nav_booking) {
                // xử lý lịch đặt
                return true;
            } else if (id == R.id.nav_profile) {
                // xử lý cá nhân
                return true;
            }
            return false;
        });

    }
}
*/
package com.example.sportbookingapp.activity;
import com.example.sportbookingapp.activity.MainActivity;
import com.example.sportbookingapp.activity.BookingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportbookingapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvPhone, tvEmail;
    private ImageView btnBack, btnEditName, btnEditPhone, btnEditEmail;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ view
        btnBack = findViewById(R.id.btn_back);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);

        btnEditName = findViewById(R.id.btn_edit_name);
        btnEditPhone = findViewById(R.id.btn_edit_phone);
        btnEditEmail = findViewById(R.id.btn_edit_email);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set thông tin tĩnh
        tvName.setText("Nguyễn Văn A");
        tvPhone.setText("0123 456 789");
        tvEmail.setText("user@example.com");

        // Nút back quay về màn hình trước
        btnBack.setOnClickListener(v -> onBackPressed());

        // Các nút edit để sửa thông tin
        btnEditName.setOnClickListener(v -> showEditDialog(tvName, "Sửa tên"));
        btnEditPhone.setOnClickListener(v -> showEditDialog(tvPhone, "Sửa số điện thoại"));
        btnEditEmail.setOnClickListener(v -> showEditDialog(tvEmail, "Sửa email"));

        // Bottom navigation chuyển màn hình
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_explore) {
                Intent intentExplore = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intentExplore);
                return true;
            } else if (id == R.id.nav_booking) {
                Intent intentBooking = new Intent(ProfileActivity.this, BookingActivity.class);
                startActivity(intentBooking);
                return true;
            } else if (id == R.id.nav_profile) {
                // Đang ở profile, không làm gì
                return true;
            }
            return false;
        });
    }

    // Hàm hiển thị dialog để sửa thông tin
    private void showEditDialog(TextView target, String title) {
        EditText editText = new EditText(this);
        editText.setText(target.getText().toString());

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newValue = editText.getText().toString().trim();
                    if (!newValue.isEmpty()) {
                        target.setText(newValue);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}


