package com.example.sportbookingapp.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.database.DatabaseHelper;
import com.example.sportbookingapp.database.UserDAO;
import com.example.sportbookingapp.model.User;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView tvStatus, tvBookingCode;
    private TextView tvBookingDate, tvTimeRange, tvDuration;
    private TextView tvFieldName, tvFieldPhone;
    private TextView tvUserName, tvUserPhone;
    private Button btnContact, btnCancel;
    private TextView tvPrice;
    private TextView tvFieldLocation, tvFieldType;

    private int bookingId;
    private String status;
    private String startTime, endTime;
    private String courtName, date;

    // Khai báo DAO để lấy thông tin User
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        // 1. Ánh xạ views
        initViews();

        // 2. Lấy dữ liệu từ Intent
        handleIntentData();

        // 3. Load thông tin người dùng thật từ Database
        loadUserInfo();

        // 4. Xử lý sự kiện các nút bấm
        setupEvents();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvBookingCode = findViewById(R.id.tvBookingCode);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvTimeRange = findViewById(R.id.tvTimeRange);
        tvDuration = findViewById(R.id.tvDuration);
        tvPrice = findViewById(R.id.tvPrice);
        tvFieldLocation = findViewById(R.id.tvFieldLocation);
        tvFieldType=findViewById(R.id.tvFieldType);
        tvFieldName = findViewById(R.id.tvFieldName);
        tvFieldPhone = findViewById(R.id.tvFieldPhone);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        btnContact = findViewById(R.id.btnContact);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("bookingId")) {
            bookingId = intent.getIntExtra("bookingId", -1);
            courtName = intent.getStringExtra("courtName");
            date = intent.getStringExtra("date");
            startTime = intent.getStringExtra("startTime");
            endTime = intent.getStringExtra("endTime");
            status = intent.getStringExtra("status");

            // Hiển thị thông tin cơ bản
            tvBookingCode.setText("Mã đặt sân: #" + bookingId);
            tvFieldName.setText(courtName);
            tvBookingDate.setText("Ngày: " + date);
            tvTimeRange.setText("Giờ: " + startTime + " - " + endTime);
            tvDuration.setText("Thời lượng: " + calculateDuration(startTime, endTime));

            // Hiển thị trạng thái & Màu sắc
            updateStatusUI(status);

            // Giá tiền
            double totalPrice = intent.getDoubleExtra("totalPrice", 0);
            tvPrice.setText("Giá: " + String.format("%,.0f", totalPrice) + " VNĐ");

            // Địa chỉ sân
            String courtAddress = intent.getStringExtra("courtAddress");
            tvFieldLocation.setText(courtAddress != null && !courtAddress.isEmpty() ? courtAddress : "Đang cập nhật");
            String courtType = intent.getStringExtra("courtType");
            tvFieldType.setText(courtType != null && !courtType.isEmpty() ? courtType : "Đang cập nhật");
            // Số điện thoại sân
            tvFieldPhone.setText("0968 68 68 68");

        } else {
            Toast.makeText(this, "Không tìm thấy thông tin booking!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // --- Load thông tin User thật từ DB ---
    private void loadUserInfo() {
        userDAO = new UserDAO(this);
        // Lấy user ID = 1 (mặc định)
        User currentUser = userDAO.getUserById(1);

        if (currentUser != null) {
            tvUserName.setText(currentUser.getFullName());
            tvUserPhone.setText(currentUser.getPhone());
        } else {
            tvUserName.setText("Khách vãng lai");
            tvUserPhone.setText("---");
        }
    }

    private void setupEvents() {
        // Nút Back: Quay về MainActivity tab Lịch sử
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent backIntent = new Intent(BookingDetailActivity.this, MainActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            backIntent.putExtra("OPEN_FRAGMENT", "History");
            startActivity(backIntent);
            finish();
        });

        // Nút liên hệ
        btnContact.setOnClickListener(v -> {
            String phone = tvFieldPhone.getText().toString();
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:" + phone));
                startActivity(callIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Hủy
        btnCancel.setOnClickListener(v -> {
            if (!"CANCELLED".equals(status)) {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận hủy")
                        .setMessage("Bạn có chắc chắn muốn hủy đặt sân này không?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> cancelBooking())
                        .setNegativeButton("Đóng", null)
                        .show();
            } else {
                Toast.makeText(this, "Lịch đã bị hủy trước đó", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatusUI(String currentStatus) {
        tvStatus.setText(statusDisplay(currentStatus));

        // Đổi màu chữ theo trạng thái
        if ("CONFIRMED".equals(currentStatus)) {
            tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Xanh lá
            btnCancel.setEnabled(true);
            btnCancel.setAlpha(1.0f);
        } else if ("CANCELLED".equals(currentStatus)) {
            tvStatus.setTextColor(Color.parseColor("#F44336")); // Đỏ
            btnCancel.setEnabled(false); // Khóa nút hủy
            btnCancel.setAlpha(0.5f); // Làm mờ nút
            btnCancel.setText("Đã hủy");
        } else {
            tvStatus.setTextColor(Color.parseColor("#FF9800")); // Cam (Pending)
        }
    }

    private void cancelBooking() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_STATUS, "CANCELLED");

        int rows = db.update(DatabaseHelper.TABLE_BOOKING, values,
                DatabaseHelper.COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(bookingId)});

        if (rows > 0) {
            Toast.makeText(this, "Hủy đặt sân thành công", Toast.LENGTH_SHORT).show();
            status = "CANCELLED";

            // Cập nhật giao diện ngay lập tức
            updateStatusUI(status);

            // Cập nhật Intent để xoay màn hình không mất trạng thái
            getIntent().putExtra("status", "CANCELLED");
        } else {
            Toast.makeText(this, "Hủy đặt sân thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private String calculateDuration(String startTime, String endTime) {
        try {
            String[] start = startTime.split(":");
            String[] end = endTime.split(":");
            int startHour = Integer.parseInt(start[0]);
            int startMin = Integer.parseInt(start[1]);
            int endHour = Integer.parseInt(end[0]);
            int endMin = Integer.parseInt(end[1]);
            int totalMin = (endHour * 60 + endMin) - (startHour * 60 + startMin);
            int hours = totalMin / 60;
            int minutes = totalMin % 60;
            if (minutes == 0) return hours + " giờ";
            return hours + " giờ " + minutes + " phút";
        } catch (Exception e) {
            return "-";
        }
    }

    private String statusDisplay(String status) {
        if (status == null) return "";
        switch (status) {
            case "CONFIRMED": return "Đã xác nhận";
            case "PENDING": return "Đang chờ";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }
}