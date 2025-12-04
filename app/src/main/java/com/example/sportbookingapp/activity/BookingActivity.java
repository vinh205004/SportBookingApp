package com.example.sportbookingapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
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

    private Court currentCourt;
    private BookingDAO bookingDAO;
    private List<TimeSlot> timeSlotList;
    private TimeSlotAdapter adapter;

    private String selectedDate; // Lưu ngày đang chọn (dd/MM/yyyy)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // 1. Nhận dữ liệu Court từ màn trước
        currentCourt = (Court) getIntent().getSerializableExtra("court_object");
        if (currentCourt == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin sân!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingDAO = new BookingDAO(this);
        initViews();

        // 2. Cấu hình ngày mặc định (Hôm nay)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        long today = System.currentTimeMillis();
        selectedDate = sdf.format(new Date(today));

        // --- QUAN TRỌNG: CHẶN CHỌN NGÀY QUÁ KHỨ ---
        // Set ngày tối thiểu có thể chọn là thời điểm hiện tại
        calendarView.setMinDate(today);

        // 3. Sự kiện chọn ngày trên lịch
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Lưu ý: month trong Calendar tính từ 0 (Tháng 1 là 0)
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = sdf.format(calendar.getTime());

            // Load lại danh sách giờ cho ngày mới chọn
            loadTimeSlots();
            // Toast.makeText(BookingActivity.this, "Đã chọn: " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        // Load lần đầu
        loadTimeSlots();

        // 4. Sự kiện nút Xác nhận
        btnConfirm.setOnClickListener(v -> handleConfirmBooking());
    }

    private void initViews() {
        calendarView = findViewById(R.id.calendarView);
        rcvTimeSlots = findViewById(R.id.rcvTimeSlots);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnConfirm = findViewById(R.id.btnConfirmBooking);

        // Hiển thị lưới 2 cột
        rcvTimeSlots.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void loadTimeSlots() {
        timeSlotList = new ArrayList<>();

        // Tạo các khung giờ cố định từ 06:00 đến 22:00
        // Trong thực tế, bạn có thể lấy giờ mở/đóng từ currentCourt.getOpenTime()
        int startHour = 6;
        int endHour = 22;

        for (int i = startHour; i < endHour; i++) {
            String start = String.format(Locale.getDefault(), "%02d:00", i);
            String end = String.format(Locale.getDefault(), "%02d:00", i + 1);

            // QUAN TRỌNG: Check Database xem giờ này đã bị đặt chưa
            // Gọi hàm isTimeSlotBooked trong BookingDAO
            boolean isBooked = bookingDAO.isTimeSlotBooked(currentCourt.getId(), selectedDate, start, end);

            timeSlotList.add(new TimeSlot(start, end, isBooked));
        }

        // Gán adapter
        adapter = new TimeSlotAdapter(timeSlotList, slot -> {
            // Callback khi click vào 1 ô giờ -> Tính lại tổng tiền
            calculateTotal();
        });
        rcvTimeSlots.setAdapter(adapter);

        // Reset tiền về 0 khi đổi ngày
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
        tvTotalAmount.setText("Tổng tiền: " + formatter.format(total) + " đ");
    }

    private void handleConfirmBooking() {
        List<TimeSlot> selectedSlots = new ArrayList<>();
        for (TimeSlot slot : timeSlotList) {
            if (slot.isSelected()) selectedSlots.add(slot);
        }

        if (selectedSlots.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 khung giờ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Giả lập UserId (Sau này lấy từ Login)
        int userId = 1;

        // Lưu từng slot thành 1 booking
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
            long result = bookingDAO.addBooking(booking);
            if (result == -1) {
                Toast.makeText(this, "Lỗi khi lưu booking: " + slot.getTimeLabel(), Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, "Đặt sân thành công!", Toast.LENGTH_LONG).show();
        finish(); // Đóng màn hình, quay về
    }
}