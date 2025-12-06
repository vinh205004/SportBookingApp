package com.example.sportbookingapp.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.adapter.BookingHistoryAdapter;
import com.example.sportbookingapp.database.BookingDAO;
import com.example.sportbookingapp.database.DatabaseHelper;
import com.example.sportbookingapp.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rcvHistory;
    private ImageView btnBack;
    private BookingDAO bookingDAO;
    private BookingHistoryAdapter adapter;
    private List<Booking> bookingList;
    private int userId = 1; // Mặc định user 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        bookingDAO = new BookingDAO(this);

        // Setup RecyclerView
        rcvHistory.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        adapter = new BookingHistoryAdapter(this, bookingList, this::showCancelDialog);
        rcvHistory.setAdapter(adapter);

        loadData();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        rcvHistory = findViewById(R.id.rcvBookingHistory);
        btnBack = findViewById(R.id.btnBackHistory);
    }

    private void loadData() {
        // Gọi hàm lấy danh sách từ DAO
        bookingList = bookingDAO.getBookingsByUser(userId);
        if (bookingList.isEmpty()) {
            Toast.makeText(this, "Bạn chưa có lịch đặt nào", Toast.LENGTH_SHORT).show();
        }
        adapter.updateList(bookingList);
    }

    private void showCancelDialog(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc chắn muốn hủy lịch đặt sân này không?")
                .setPositiveButton("Hủy sân", (dialog, which) -> {
                    cancelBooking(booking);
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelBooking(Booking booking) {
        // Cập nhật trạng thái trong Database
        // Lưu ý: Cần thêm hàm updateStatus trong BookingDAO,
        // hoặc dùng code nhanh ở đây:
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_STATUS, "CANCELLED");

        int rows = db.update(DatabaseHelper.TABLE_BOOKING, values,
                DatabaseHelper.COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(booking.getId())});

        if (rows > 0) {
            Toast.makeText(this, "Đã hủy lịch thành công!", Toast.LENGTH_SHORT).show();
            loadData(); // Load lại danh sách
        } else {
            Toast.makeText(this, "Lỗi khi hủy!", Toast.LENGTH_SHORT).show();
        }
    }
}