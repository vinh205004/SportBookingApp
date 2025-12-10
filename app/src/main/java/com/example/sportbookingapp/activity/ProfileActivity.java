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
        btnBack = findViewById(R.id.btnBackBooking);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmail = findViewById(R.id.tv_email);

        btnEditName = findViewById(R.id.btn_edit_name);
        btnEditPhone = findViewById(R.id.btn_edit_phone);
        btnEditEmail = findViewById(R.id.btn_edit_email);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // ⭐ Quan trọng: tô màu nút "Cá nhân"
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        // Set thông tin tĩnh
        tvName.setText("Nguyễn Văn A");
        tvPhone.setText("0123 456 789");
        tvEmail.setText("user@example.com");

        // Nút back
        btnBack.setOnClickListener(v -> onBackPressed());

        // Nút edit
        btnEditName.setOnClickListener(v -> showEditDialog(tvName, "Sửa tên"));
        btnEditPhone.setOnClickListener(v -> showEditDialog(tvPhone, "Sửa số điện thoại"));
        btnEditEmail.setOnClickListener(v -> showEditDialog(tvEmail, "Sửa email"));

        // ⭐ Bottom Navigation chuyển màn hình đúng
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_explore) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;

            } else if (id == R.id.nav_booking) {
                Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;

            } else if (id == R.id.nav_profile) {
                return true; // Đang ở đây rồi
            }

            return false;
        });
    }

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
