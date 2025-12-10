package com.example.sportbookingapp.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent; // Thêm import
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.adapter.BookingHistoryAdapter;
import com.example.sportbookingapp.database.BookingDAO;
import com.example.sportbookingapp.database.DatabaseHelper;
import com.example.sportbookingapp.model.Booking;
import com.google.android.material.bottomnavigation.BottomNavigationView; // Thêm import

import java.util.ArrayList;
import java.util.List;
import com.example.sportbookingapp.model.Court;
import com.example.sportbookingapp.database.CourtDAO;
import com.example.sportbookingapp.model.Court;



public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rcvHistory;
    private ImageView btnBack;
    private TextView tabUpcoming, tabHistory;
    private BottomNavigationView bottomNavigationView; // Khai báo Menu đáy

    private BookingDAO bookingDAO;
    private BookingHistoryAdapter adapter;

    private List<Booking> allBookings;
    private List<Booking> displayList;
    private int userId = 1;
    private boolean isShowingUpcoming = true;
    private CourtDAO courtDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        courtDAO = new CourtDAO(this);

        initViews();
        bookingDAO = new BookingDAO(this);

        rcvHistory.setLayoutManager(new LinearLayoutManager(this));
        allBookings = new ArrayList<>();
        displayList = new ArrayList<>();

        adapter = new BookingHistoryAdapter(this, displayList, this::showCancelDialog);
        rcvHistory.setAdapter(adapter);

        loadData();
        setupEvents();

        // --- CẤU HÌNH MENU ĐÁY ---
        // Đánh dấu mục "Lịch đặt" là đang chọn
        bottomNavigationView.setSelectedItemId(R.id.nav_booking);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_explore) {
                // Chuyển về Trang chủ
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                // Xóa stack để khi back không quay lại đây nữa (tùy chọn)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Đóng màn hình này lại
                return true;
            } else if (id == R.id.nav_booking) {
                return true; // Đang ở đây rồi
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(HistoryActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });

    }

    private void initViews() {
        rcvHistory = findViewById(R.id.rcvBookingHistory);
        btnBack = findViewById(R.id.btnBackHistory);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabHistory = findViewById(R.id.tabHistory);
        bottomNavigationView = findViewById(R.id.bottom_navigation); // Ánh xạ
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> {
            // Khi bấm nút Back (mũi tên trên cùng), quay về trang chủ
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        tabUpcoming.setOnClickListener(v -> {
            if (!isShowingUpcoming) {
                isShowingUpcoming = true;
                updateTabUI();
                filterData();
            }
        });

        tabHistory.setOnClickListener(v -> {
            if (isShowingUpcoming) {
                isShowingUpcoming = false;
                updateTabUI();
                filterData();
            }
        });
    }

    private void updateTabUI() {
        if (isShowingUpcoming) {
            tabUpcoming.setBackgroundResource(R.drawable.bg_card_info_item);
            tabUpcoming.setTypeface(null, Typeface.BOLD);
            tabUpcoming.setTextColor(Color.parseColor("#333333"));

            tabHistory.setBackground(null);
            tabHistory.setTypeface(null, Typeface.NORMAL);
            tabHistory.setTextColor(Color.parseColor("#757575"));
        } else {
            tabHistory.setBackgroundResource(R.drawable.bg_card_info_item);
            tabHistory.setTypeface(null, Typeface.BOLD);
            tabHistory.setTextColor(Color.parseColor("#333333"));

            tabUpcoming.setBackground(null);
            tabUpcoming.setTypeface(null, Typeface.NORMAL);
            tabUpcoming.setTextColor(Color.parseColor("#757575"));
        }
    }

    private void loadData() {
        allBookings = bookingDAO.getBookingsByUser(userId);

        // --- Gán courtObject cho từng booking ---
        for (Booking booking : allBookings) {
            Court court = courtDAO.getCourtById(booking.getCourtId());
            booking.setCourtObject(court);
        }

        filterData();
    }



    private void filterData() {
        displayList.clear();
        for (Booking b : allBookings) {
            if (isShowingUpcoming) {
                if ("CONFIRMED".equals(b.getStatus())) {
                    displayList.add(b);
                }
            } else {
                if ("CANCELLED".equals(b.getStatus())) {
                    displayList.add(b);
                }
            }
        }
        adapter.updateList(displayList);
    }

    private void showCancelDialog(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có muốn hủy lịch: " + booking.getCourtName() + " lúc " + booking.getStartTime() + "?")
                .setPositiveButton("Đồng ý", (dialog, which) -> cancelBooking(booking))
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelBooking(Booking booking) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_STATUS, "CANCELLED");

        int rows = db.update(DatabaseHelper.TABLE_BOOKING, values,
                DatabaseHelper.COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(booking.getId())});

        if (rows > 0) {
            Toast.makeText(this, "Đã hủy thành công", Toast.LENGTH_SHORT).show();
            loadData();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_booking);
    }

}