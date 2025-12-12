package com.example.sportbookingapp.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.adapter.BookingHistoryAdapter;
import com.example.sportbookingapp.database.BookingDAO;
import com.example.sportbookingapp.database.CourtDAO;
import com.example.sportbookingapp.database.DatabaseHelper;
import com.example.sportbookingapp.model.Booking;
import com.example.sportbookingapp.model.Court;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView rcvHistory;
    private ImageView btnBack;
    private TextView tabUpcoming, tabHistory;
    // Đã xóa BottomNavigationView

    private BookingDAO bookingDAO;
    private CourtDAO courtDAO;
    private BookingHistoryAdapter adapter;

    private List<Booking> allBookings;
    private List<Booking> displayList;
    private int userId = 1; // Giả định user ID = 1
    private boolean isShowingUpcoming = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Nạp giao diện
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // 2. Khởi tạo DAO (Dùng requireContext)
        courtDAO = new CourtDAO(requireContext());
        bookingDAO = new BookingDAO(requireContext());

        // 3. Ánh xạ View
        initViews(view);

        // 4. Setup RecyclerView
        rcvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        allBookings = new ArrayList<>();
        displayList = new ArrayList<>();

        // Adapter
        adapter = new BookingHistoryAdapter(requireContext(), displayList, this::showCancelDialog);
        rcvHistory.setAdapter(adapter);

        // 5. Load Data & Sự kiện
        loadData();
        setupEvents();

        return view;
    }

    private void initViews(View view) {
        // Phải tìm view từ biến 'view'
        rcvHistory = view.findViewById(R.id.rcvBookingHistory);
        btnBack = view.findViewById(R.id.btnBackHistory);
        tabUpcoming = view.findViewById(R.id.tabUpcoming);
        tabHistory = view.findViewById(R.id.tabHistory);
    }

    private void setupEvents() {
        // Nút Back: Quay về HomeFragment
        btnBack.setOnClickListener(v -> {
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav
                    = requireActivity().findViewById(R.id.bottom_navigation);

            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_explore);
            }
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
            tabUpcoming.setBackgroundResource(R.drawable.bg_card_info_item); // Đảm bảo file drawable này tồn tại
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

        // Gán object Court vào Booking để hiển thị tên sân, ảnh sân
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
                // Chỉ hiện đơn ĐÃ XÁC NHẬN (CONFIRMED)
                if ("CONFIRMED".equals(b.getStatus())) {
                    displayList.add(b);
                }
            } else {
                // Hiện đơn ĐÃ HỦY hoặc ĐÃ HOÀN THÀNH
                if ("CANCELLED".equals(b.getStatus()) || "COMPLETED".equals(b.getStatus())) {
                    displayList.add(b);
                }
            }
        }
        adapter.updateList(displayList);
    }

    private void showCancelDialog(Booking booking) {
        new AlertDialog.Builder(requireContext()) // Dùng requireContext()
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có muốn hủy lịch: " + booking.getCourtName() + " lúc " + booking.getStartTime() + "?")
                .setPositiveButton("Đồng ý", (dialog, which) -> cancelBooking(booking))
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelBooking(Booking booking) {
        // Cập nhật trạng thái hủy vào DB
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BOOKING_STATUS, "CANCELLED");

        int rows = db.update(DatabaseHelper.TABLE_BOOKING, values,
                DatabaseHelper.COL_BOOKING_ID + "=?",
                new String[]{String.valueOf(booking.getId())});

        if (rows > 0) {
            Toast.makeText(requireContext(), "Đã hủy thành công", Toast.LENGTH_SHORT).show();
            loadData(); // Load lại danh sách để cập nhật giao diện
        }
    }
}