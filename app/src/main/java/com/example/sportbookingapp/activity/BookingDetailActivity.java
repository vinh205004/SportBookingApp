package com.example.sportbookingapp.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.database.DatabaseHelper;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView tvStatus, tvBookingCode;
    private TextView tvBookingDate, tvTimeRange, tvDuration, tvPaymentMethod;
    private TextView tvFieldName, tvFieldPhone;
    private TextView tvUserName, tvUserPhone;
    private Button btnContact, btnCancel;

    private int bookingId;
    private String status;
    private String courtName, date;
    private TextView tvPrice;
    private TextView tvFieldLocation;
    private ImageView imgCourt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        // --- Ánh xạ views ---
        initViews();

        // --- Lấy dữ liệu từ Intent ---
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("bookingId")) {
            bookingId = intent.getIntExtra("bookingId", -1);
            courtName = intent.getStringExtra("courtName");
            date = intent.getStringExtra("date");
            status = intent.getStringExtra("status");

            tvBookingCode.setText(getString(R.string.booking_code_prefix) + bookingId);
            tvFieldName.setText(courtName);
            tvBookingDate.setText("Ngày: " + date);
            tvTimeRange.setText("Giờ: " + intent.getStringExtra("startTime") + " - " + intent.getStringExtra("endTime"));
            tvDuration.setText("Thời lượng: " + calculateDuration(intent.getStringExtra("startTime"), intent.getStringExtra("endTime")));
            
            updateStatusDisplay(status);
            
            double totalPrice = intent.getDoubleExtra("totalPrice", 0);
            tvPrice.setText("Giá: " + String.format("%,.0f", totalPrice) + " VNĐ");

            String paymentMethod = intent.getStringExtra("paymentMethod");
            tvPaymentMethod.setText(paymentMethod);
            
            String courtImage = intent.getStringExtra("courtImage");
            int resId = getResources().getIdentifier(courtImage, "drawable", getPackageName());
            if (resId != 0) {
                imgCourt.setImageResource(resId);
            } else {
                imgCourt.setImageResource(R.drawable.ic_launcher_background);
            }

            String courtAddress = intent.getStringExtra("courtAddress");
            tvFieldLocation.setText(courtAddress != null && !courtAddress.isEmpty() ? courtAddress : "Địa chỉ chưa có");

            tvFieldPhone.setText("0123456789");
            tvUserName.setText("Nguyễn Văn A");
            tvUserPhone.setText("0987654321");

        } else {
            Toast.makeText(this, "Không tìm thấy thông tin booking!", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupEvents();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvBookingCode = findViewById(R.id.tvBookingCode);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvTimeRange = findViewById(R.id.tvTimeRange);
        tvDuration = findViewById(R.id.tvDuration);
        tvPrice = findViewById(R.id.tvPrice);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        imgCourt = findViewById(R.id.imgField);
        tvFieldLocation = findViewById(R.id.tvFieldLocation);
        tvFieldName = findViewById(R.id.tvFieldName);
        tvFieldPhone = findViewById(R.id.tvFieldPhone);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        btnContact = findViewById(R.id.btnContact);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupEvents() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnContact.setOnClickListener(v -> {
            String phone = tvFieldPhone.getText().toString();
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(callIntent);
        });

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

    private void updateStatusDisplay(String status) {
        View statusContainer = findViewById(R.id.status_container);
        switch (status) {
            case "CONFIRMED": 
                tvStatus.setText("Đã xác nhận");
                tvStatus.setTextColor(Color.parseColor("#047857"));
                statusContainer.setBackgroundResource(R.drawable.status_green);
                break;
            case "PENDING": 
                tvStatus.setText("Đang chờ");
                tvStatus.setTextColor(Color.parseColor("#D97706"));
                statusContainer.setBackgroundResource(R.drawable.status_yellow);
                break;
            case "CANCELLED": 
                tvStatus.setText("Đã hủy");
                tvStatus.setTextColor(Color.parseColor("#DC2626"));
                statusContainer.setBackgroundResource(R.drawable.status_red);
                btnCancel.setEnabled(false);
                btnCancel.setAlpha(0.5f);
                break;
            default: 
                tvStatus.setText(status);
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
            updateStatusDisplay(status);
        } else {
            Toast.makeText(this, "Hủy đặt sân thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
