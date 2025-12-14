package com.example.sportbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.adapter.TimeSlotAdapter;
import com.example.sportbookingapp.database.BookingDAO;
import com.example.sportbookingapp.model.Court;
import com.example.sportbookingapp.model.TimeSlot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView rcvTimeSlots;
    private TextView tvTotalAmount;
    private Button btnConfirm;
    private ImageView btnBack;

    private Court currentCourt;
    private BookingDAO bookingDAO;
    private List<TimeSlot> timeSlotList;
    private TimeSlotAdapter adapter;

    private String selectedDate;
    private double currentTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        currentCourt = (Court) getIntent().getSerializableExtra("court_object");
        if (currentCourt == null) {
            finish(); return;
        }

        bookingDAO = new BookingDAO(this);
        initViews();

        // Cấu hình ngày
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        long today = System.currentTimeMillis();
        selectedDate = sdf.format(new Date(today));
        calendarView.setMinDate(today);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = sdf.format(calendar.getTime());
            loadTimeSlots();
        });

        loadTimeSlots();

        btnBack.setOnClickListener(v -> finish());

        // --- Nút tiếp tục chuyển sang màn hình Xác nhận ---
        btnConfirm.setOnClickListener(v -> goToConfirmScreen());
    }

    private void initViews() {
        calendarView = findViewById(R.id.calendarView);
        rcvTimeSlots = findViewById(R.id.rcvTimeSlots);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnConfirm = findViewById(R.id.btnConfirmBooking);
        btnBack = findViewById(R.id.btnBackBooking);
        rcvTimeSlots.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadTimeSlots() {
        timeSlotList = new ArrayList<>();
        int startHour = 6; int endHour = 22;

        for (int i = startHour; i < endHour; i++) {
            String start = String.format(Locale.getDefault(), "%02d:00", i);
            String end = String.format(Locale.getDefault(), "%02d:00", i + 1);
            boolean isBooked = bookingDAO.isTimeSlotBooked(currentCourt.getId(), selectedDate, start, end);
            timeSlotList.add(new TimeSlot(start, end, isBooked));
        }
        adapter = new TimeSlotAdapter(timeSlotList, slot -> calculateTotal());
        rcvTimeSlots.setAdapter(adapter);
        calculateTotal();
    }

    private void calculateTotal() {
        currentTotal = 0;
        for (TimeSlot slot : timeSlotList) {
            if (slot.isSelected()) currentTotal += currentCourt.getPrice();
        }
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvTotalAmount.setText(formatter.format(currentTotal) + " đ");
    }

    private void goToConfirmScreen() {
        // Lấy danh sách các khung giờ đã chọn
        ArrayList<String> selectedSlots = new ArrayList<>();
        for (TimeSlot slot : timeSlotList) {
            if (slot.isSelected()) {
                selectedSlots.add(slot.getTimeLabel()); // "08:00 - 09:00"
            }
        }

        if (selectedSlots.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn khung giờ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển dữ liệu sang màn hình xác nhận
        Intent intent = new Intent(BookingActivity.this, ConfirmBookingActivity.class);
        intent.putExtra("court_object", currentCourt);
        intent.putExtra("date", selectedDate);
        intent.putStringArrayListExtra("slots", selectedSlots);
        intent.putExtra("total_price", currentTotal);
        startActivity(intent);
    }
}