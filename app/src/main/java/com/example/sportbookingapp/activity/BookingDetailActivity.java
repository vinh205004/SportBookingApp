package com.example.sportbookingapp.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.database.DatabaseHelper;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView tvStatus, tvBookingCode;
    private TextView tvBookingDate, tvTimeRange, tvDuration;
    private TextView tvFieldName, tvFieldPhone;
    private TextView tvUserName, tvUserPhone;
    private Button btnContact, btnCancel;

    private int bookingId;
    private String status;
    private String startTime, endTime;
    private String courtName, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        // --- Ánh xạ views ---
        tvStatus = findViewById(R.id.tvStatus);
        tvBookingCode = findViewById(R.id.tvBookingCode);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvTimeRange = findViewById(R.id.tvTimeRange);
        tvDuration = findViewById(R.id.tvDuration);
        tvFieldName = findViewById(R.id.tvFieldName);
        tvFieldPhone = findViewById(R.id.tvFieldPhone);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        btnContact = findViewById(R.id.btnContact);
        btnCancel = findViewById(R.id.btnCancel);

        // --- Lấy dữ liệu từ Intent ---
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("bookingId")) {
            bookingId = intent.getIntExtra("bookingId", -1);
            courtName = intent.getStringExtra("courtName");
            date = intent.getStringExtra("date");
            startTime = intent.getStringExtra("startTime");
            endTime = intent.getStringExtra("endTime");
            status = intent.getStringExtra("status");

            tvBookingCode.setText("Mã đặt sân: #" + bookingId);
            tvFieldName.setText(courtName);
            tvBookingDate.setText("Ngày: " + date);
            tvTimeRange.setText("Giờ: " + startTime + " - " + endTime);
            tvDuration.setText("Thời lượng: " + calculateDuration(startTime, endTime));
            tvStatus.setText(statusDisplay(status));

            // Số điện thoại sân giả lập
            tvFieldPhone.setText("0123456789");

            // Thông tin user tĩnh
            tvUserName.setText("Nguyễn Văn A");
            tvUserPhone.setText("0987654321");

        } else {
            Toast.makeText(this, "Không tìm thấy thông tin booking!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // --- Nút quay về HistoryActivity ---
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent backIntent = new Intent(BookingDetailActivity.this, HistoryActivity.class);
            startActivity(backIntent);
            finish();
        });

        // --- Nút liên hệ sân ---
        btnContact.setOnClickListener(v -> {
            String phone = tvFieldPhone.getText().toString();
            Intent callIntent = new Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:" + phone));
            startActivity(callIntent);
        });

        // --- Nút hủy booking ---
        btnCancel.setOnClickListener(v -> {
            if (!status.equals("CANCELLED")) {
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
        switch (status) {
            case "CONFIRMED": return "Đã xác nhận";
            case "PENDING": return "Đang chờ";
            case "CANCELLED": return "Đã hủy";
            default: return status;
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
            tvStatus.setText("Đã hủy");
            btnCancel.setEnabled(false);
        } else {
            Toast.makeText(this, "Hủy đặt sân thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
