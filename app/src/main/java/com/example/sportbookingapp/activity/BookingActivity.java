package com.example.sportbookingapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView; // Nhớ import ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.adapter.TimeSlotAdapter;
import com.example.sportbookingapp.database.BookingDAO;
import com.example.sportbookingapp.model.Booking;
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
    private ImageView btnBack; // 1. Khai báo biến nút Back

    private Court currentCourt;
    private BookingDAO bookingDAO;
    private List<TimeSlot> timeSlotList;
    private TimeSlotAdapter adapter;

    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        currentCourt = (Court) getIntent().getSerializableExtra("court_object");
        if (currentCourt == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin sân!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingDAO = new BookingDAO(this);
        initViews();

        // Cấu hình ngày
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        long today = System.currentTimeMillis();
        selectedDate = sdf.format(new Date(today));
        calendarView.setMinDate(today); // Chặn ngày quá khứ

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = sdf.format(calendar.getTime());
            loadTimeSlots();
        });

        loadTimeSlots();

        btnConfirm.setOnClickListener(v -> handleConfirmBooking());

        // 3. Xử lý sự kiện nút Back -> Đóng màn hình
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        calendarView = findViewById(R.id.calendarView);
        rcvTimeSlots = findViewById(R.id.rcvTimeSlots);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnConfirm = findViewById(R.id.btnConfirmBooking);

        // 2. Ánh xạ view từ XML
        btnBack = findViewById(R.id.btnBackBooking);

        rcvTimeSlots.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadTimeSlots() {
        timeSlotList = new ArrayList<>();
        int startHour = 6;
        int endHour = 22;

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
        double total = 0;
        for (TimeSlot slot : timeSlotList) {
            if (slot.isSelected()) {
                total += currentCourt.getPrice();
            }
        }
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvTotalAmount.setText(formatter.format(total) + " đ");
    }

    private void handleConfirmBooking() {
        List<TimeSlot> selectedSlots = new ArrayList<>();
        for (TimeSlot slot : timeSlotList) {
            if (slot.isSelected()) selectedSlots.add(slot);
        }

        if (selectedSlots.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn khung giờ!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = 1;
        for (TimeSlot slot : selectedSlots) {
            Booking booking = new Booking(
                    userId,
                    currentCourt.getId(),
                    currentCourt.getName(),
                    currentCourt.getImageName(),
                    selectedDate,
                    slot.getStartTime(),
                    slot.getEndTime(),
                    currentCourt.getPrice(),
                    "CONFIRMED",
                    "CASH"
            );
            bookingDAO.addBooking(booking);
        }

        Toast.makeText(this, "Đặt sân thành công!", Toast.LENGTH_LONG).show();
        finish();
    }
}