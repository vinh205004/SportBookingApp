package com.example.sportbookingapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.database.BookingDAO;
import com.example.sportbookingapp.database.UserDAO;
import com.example.sportbookingapp.model.Booking;
import com.example.sportbookingapp.model.Court;
import com.example.sportbookingapp.model.User;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ConfirmBookingActivity extends AppCompatActivity {

    private TextView tvCourtName, tvCourtAddress, tvDate, tvTime, tvTotalPrice;
    private TextView tvDiscountMessage;
    private EditText edtName, edtPhone;
    private RadioGroup rgPayment;
    private Button btnConfirm;
    private ImageView btnBack;

    private Court currentCourt;
    private String selectedDate;
    private ArrayList<String> selectedSlots;
    private double originalPrice; // Giá gốc
    private double finalPrice;    // Giá sau khi tính toán

    // Biến cờ để đánh dấu đơn này có được giảm giá hay không (0: Không, 1: Có)
    private int isDiscountApplied = 0;

    private BookingDAO bookingDAO;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        // 1. Nhận dữ liệu
        Intent intent = getIntent();
        currentCourt = (Court) intent.getSerializableExtra("court_object");
        selectedDate = intent.getStringExtra("date");
        selectedSlots = intent.getStringArrayListExtra("slots");
        originalPrice = intent.getDoubleExtra("total_price", 0);

        if (currentCourt == null || selectedSlots == null || selectedSlots.isEmpty()) {
            Toast.makeText(this, "Lỗi dữ liệu đặt sân", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Khởi tạo DAO
        bookingDAO = new BookingDAO(this);
        userDAO = new UserDAO(this);

        initViews();

        // 3. Tự động điền user
        autoFillUserInfo();

        // 4. QUAN TRỌNG: Tính toán giảm giá theo thuật toán mới
        calculateDiscountAndDisplay();

        // 5. Sự kiện
        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> processBooking());
    }

    private void initViews() {
        tvCourtName = findViewById(R.id.tvCourtNameConfirm);
        tvCourtAddress = findViewById(R.id.tvCourtAddressConfirm);
        tvDate = findViewById(R.id.tvDateConfirm);
        tvTime = findViewById(R.id.tvTimeConfirm);
        tvTotalPrice = findViewById(R.id.tvTotalPriceConfirm);
        tvDiscountMessage = findViewById(R.id.tvDiscountMessage);

        edtName = findViewById(R.id.edtUserName);
        edtPhone = findViewById(R.id.edtUserPhone);
        rgPayment = findViewById(R.id.radioGroupPayment);

        btnConfirm = findViewById(R.id.btnFinalConfirm);
        btnBack = findViewById(R.id.btnBackConfirm);
    }

    private void autoFillUserInfo() {
        User user = userDAO.getUserById(1);
        if (user != null) {
            edtName.setText(user.getFullName());
            edtPhone.setText(user.getPhone());
        }
    }

    // --- LOGIC HẬU MÃI ---
    private void calculateDiscountAndDisplay() {
        // Hiển thị thông tin sân cơ bản
        tvCourtName.setText(currentCourt.getName());
        tvCourtAddress.setText(currentCourt.getAddress());
        tvDate.setText(selectedDate);

        StringBuilder slotsStr = new StringBuilder();
        for (String slot : selectedSlots) {
            if (slotsStr.length() > 0) slotsStr.append(", ");
            slotsStr.append(slot);
        }
        tvTime.setText(slotsStr.toString());

        int userId = 1; // User mặc định

        // 1. Lấy số liệu thực tế từ Database
        // Số đơn đang hoạt động (trừ đơn hủy)
        int currentActive = bookingDAO.getBookingCount(userId);
        // Số đơn ĐÃ được hưởng giảm giá trong quá khứ
        int currentDiscounted = bookingDAO.countDiscountedBookings(userId);

        // 2. Dự tính tổng số đơn sau khi đặt thành công đơn này
        int newTotal = currentActive + 1;

        // 3. Tính "Quota" (Số lượng đơn giảm giá tối đa được phép có)
        // Quy tắc: Cứ 6 đơn thì được 1 đơn giảm (Tỉ lệ 1/6)
        int maxAllowedDiscount = newTotal / 6;

        DecimalFormat formatter = new DecimalFormat("###,###,###");

        // 4. So sánh: Nếu số đơn giảm giá hiện tại < Số tối đa cho phép -> ĐƯỢC GIẢM
        if (currentDiscounted < maxAllowedDiscount) {
            // --- TRƯỜNG HỢP ĐƯỢC GIẢM ---
            isDiscountApplied = 1; // Bật cờ lên để lát lưu vào DB

            double discountAmount = originalPrice * 0.2; // Giảm 20%
            finalPrice = originalPrice - discountAmount;

            tvDiscountMessage.setVisibility(View.VISIBLE);
            tvDiscountMessage.setText("🎉 ƯU ĐÃI ĐẶC BIỆT: Bạn được giảm ngay 20% cho đơn hàng này!");
            tvDiscountMessage.setTextColor(Color.parseColor("#F44336"));
            tvDiscountMessage.setBackgroundColor(Color.parseColor("#FFEBEE"));

            String oldPriceStr = formatter.format(originalPrice) + "đ";
            String newPriceStr = formatter.format(finalPrice) + " VNĐ";

            tvTotalPrice.setText(oldPriceStr + "   ->   " + newPriceStr);

        } else {
            // --- TRƯỜNG HỢP KHÔNG ĐƯỢC GIẢM ---
            isDiscountApplied = 0; // Tắt cờ
            finalPrice = originalPrice; // Giữ nguyên giá gốc

            // Tính số đơn cần đặt thêm để được giảm
            // Công thức: (Số đơn giảm giá hiện tại + 1) * 6 - Tổng hiện tại
            int targetTotal = (currentDiscounted + 1) * 6;
            int remaining = targetTotal - newTotal;
            if (remaining <= 0) remaining = 6; // Fix hiển thị

            tvDiscountMessage.setVisibility(View.VISIBLE);
            tvDiscountMessage.setTextColor(Color.parseColor("#757575")); // Màu xám
            tvDiscountMessage.setBackgroundColor(Color.TRANSPARENT);
            tvDiscountMessage.setText("(Tích điểm: Cần đặt thêm " + remaining + " đơn hợp lệ nữa để nhận ưu đãi 20%)");

            tvTotalPrice.setText(formatter.format(finalPrice) + " VNĐ");
        }
    }

    private void processBooking() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ tên và số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = "COD";
        int selectedId = rgPayment.getCheckedRadioButtonId();
        if (selectedId == R.id.rbWallet) paymentMethod = "Banking";
        else if (selectedId == R.id.rbCredit) paymentMethod = "Banking";

        int userId = 1;

        // Chia đều giá tiền cho các slot (nếu đặt nhiều slot 1 lúc)
        double pricePerSlot = finalPrice / selectedSlots.size();

        for (String slotStr : selectedSlots) {
            String[] times = slotStr.split(" - ");
            if (times.length < 2) continue;

            String start = times[0].trim();
            String end = times[1].trim();

            Booking booking = new Booking(
                    userId,
                    currentCourt.getId(),
                    currentCourt.getName(),
                    currentCourt.getImageName(),
                    selectedDate,
                    start,
                    end,
                    pricePerSlot,
                    "CONFIRMED",
                    paymentMethod
            );

            booking.setIsDiscounted(isDiscountApplied);

            booking.setCourtObject(currentCourt);
            bookingDAO.addBooking(booking);
        }

        Toast.makeText(this, "Đặt sân thành công!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("OPEN_FRAGMENT", "History");
        startActivity(intent);
        finish();
    }
}