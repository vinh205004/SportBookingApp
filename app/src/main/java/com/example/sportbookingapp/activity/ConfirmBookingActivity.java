package com.example.sportbookingapp.activity;

import android.content.Intent;
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
import com.example.sportbookingapp.model.Booking;
import com.example.sportbookingapp.model.Court;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfirmBookingActivity extends AppCompatActivity {

    private TextView tvCourtName, tvCourtAddress, tvDate, tvTime, tvTotalPrice, tvDiscountAmount;
    private EditText edtName, edtPhone, edtPromoCode;
    private RadioGroup rgPayment;
    private Button btnConfirm, btnApplyPromo;
    private ImageView btnBack;

    private Court currentCourt;
    private String selectedDate;
    private ArrayList<String> selectedSlots;
    private double totalPrice;
    private double discountAmount = 0;
    private double finalPrice;
    private BookingDAO bookingDAO;
    private boolean isPromoApplied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        Intent intent = getIntent();
        currentCourt = (Court) intent.getSerializableExtra("court_object");
        selectedDate = intent.getStringExtra("date");
        selectedSlots = intent.getStringArrayListExtra("slots");
        totalPrice = intent.getDoubleExtra("total_price", 0);
        finalPrice = totalPrice;

        if (currentCourt == null || selectedSlots == null || selectedSlots.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_data_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingDAO = new BookingDAO(this);
        initViews();
        displayData();

        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> processBooking());
        btnApplyPromo.setOnClickListener(v -> applyPromoCode());
    }

    private void initViews() {
        tvCourtName = findViewById(R.id.tvCourtNameConfirm);
        tvCourtAddress = findViewById(R.id.tvCourtAddressConfirm);
        tvDate = findViewById(R.id.tvDateConfirm);
        tvTime = findViewById(R.id.tvTimeConfirm);
        tvTotalPrice = findViewById(R.id.tvTotalPriceConfirm);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);

        edtName = findViewById(R.id.edtUserName);
        edtPhone = findViewById(R.id.edtUserPhone);
        edtPromoCode = findViewById(R.id.edtPromoCode);
        rgPayment = findViewById(R.id.radioGroupPayment);

        btnConfirm = findViewById(R.id.btnFinalConfirm);
        btnApplyPromo = findViewById(R.id.btnApplyPromo);
        btnBack = findViewById(R.id.btnBackConfirm);
    }

    private void displayData() {
        tvCourtName.setText(currentCourt.getName());
        tvCourtAddress.setText(currentCourt.getAddress());
        tvDate.setText(selectedDate);

        StringBuilder slotsStr = new StringBuilder();
        for (String slot : selectedSlots) {
            if (slotsStr.length() > 0) slotsStr.append(", ");
            slotsStr.append(slot);
        }
        tvTime.setText(slotsStr.toString());

        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        
        if (discountAmount > 0) {
            tvDiscountAmount.setText("- " + formatter.format(discountAmount) + " VNĐ");
            tvDiscountAmount.setVisibility(View.VISIBLE);
        } else {
            tvDiscountAmount.setVisibility(View.GONE);
        }
        
        tvTotalPrice.setText(formatter.format(finalPrice) + " VNĐ");
    }

    private void applyPromoCode() {
        String code = edtPromoCode.getText().toString().trim().toUpperCase();

        if (code.isEmpty()) {
            return;
        }

        if (isPromoApplied) {
            Toast.makeText(this, "Bạn đã nhập mã rồi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.equals("SPORT2024")) {
            // Giảm 20%
            discountAmount = totalPrice * 0.2;
            finalPrice = totalPrice - discountAmount;
            isPromoApplied = true;
            Toast.makeText(this, getString(R.string.msg_promo_success), Toast.LENGTH_SHORT).show();
            updatePriceDisplay();
            edtPromoCode.setEnabled(false); // Khóa không cho nhập lại
            btnApplyPromo.setEnabled(false);
        } else if (code.equals("WELCOME")) {
            // Giảm 50k
            discountAmount = 50000;
            if (discountAmount > totalPrice) discountAmount = totalPrice; // Không giảm quá tiền gốc
            finalPrice = totalPrice - discountAmount;
            isPromoApplied = true;
            Toast.makeText(this, getString(R.string.msg_promo_success), Toast.LENGTH_SHORT).show();
            updatePriceDisplay();
            edtPromoCode.setEnabled(false);
            btnApplyPromo.setEnabled(false);
        } else {
            Toast.makeText(this, getString(R.string.msg_promo_invalid), Toast.LENGTH_SHORT).show();
        }
    }

    private void processBooking() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_missing_info), Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = getString(R.string.payment_cash);
        int selectedId = rgPayment.getCheckedRadioButtonId();
        if (selectedId == R.id.rbWallet) paymentMethod = getString(R.string.payment_wallet);
        else if (selectedId == R.id.rbCredit) paymentMethod = getString(R.string.payment_credit);

        int userId = 1;

        // 1. Sắp xếp lại danh sách khung giờ theo thời gian
        Collections.sort(selectedSlots); // Vì định dạng "06:00 - 07:00" nên sort string cũng OK

        // 2. Thuật toán gộp các slot liên tiếp
        List<Booking> bookingListToSave = new ArrayList<>();
        if (!selectedSlots.isEmpty()) {
            String currentStart = getStartTime(selectedSlots.get(0));
            String currentEnd = getEndTime(selectedSlots.get(0));
            int slotsCount = 1;

            for (int i = 1; i < selectedSlots.size(); i++) {
                String nextStart = getStartTime(selectedSlots.get(i));
                String nextEnd = getEndTime(selectedSlots.get(i));

                if (currentEnd.equals(nextStart)) {
                    // Liên tiếp -> Gộp: cập nhật giờ kết thúc mới
                    currentEnd = nextEnd;
                    slotsCount++;
                } else {
                    // Không liên tiếp -> Lưu booking cũ và bắt đầu booking mới
                    double priceForThisBooking = (finalPrice / selectedSlots.size()) * slotsCount;
                    bookingListToSave.add(createBookingObject(userId, currentStart, currentEnd, priceForThisBooking, paymentMethod));
                    
                    // Reset cho vòng lặp mới
                    currentStart = nextStart;
                    currentEnd = nextEnd;
                    slotsCount = 1;
                }
            }
            // Lưu booking cuối cùng
            double priceForLastBooking = (finalPrice / selectedSlots.size()) * slotsCount;
            bookingListToSave.add(createBookingObject(userId, currentStart, currentEnd, priceForLastBooking, paymentMethod));
        }

        // 3. Lưu vào DB
        for (Booking b : bookingListToSave) {
            bookingDAO.addBooking(b);
        }

        Toast.makeText(this, getString(R.string.msg_booking_success), Toast.LENGTH_LONG).show();

        // Chuyển hướng về MainActivity và yêu cầu mở tab Lịch sử (tab index 1)
        Intent intentMain = new Intent(this, MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intentMain.putExtra("open_tab", 1); // 1 = History Tab
        startActivity(intentMain);

        finish();
    }

    private String getStartTime(String slotStr) {
        return slotStr.split(" - ")[0].trim();
    }

    private String getEndTime(String slotStr) {
        return slotStr.split(" - ")[1].trim();
    }

    private Booking createBookingObject(int userId, String start, String end, double price, String paymentMethod) {
        Booking booking = new Booking(
                userId,
                currentCourt.getId(),
                currentCourt.getName(),
                currentCourt.getImageName(),
                selectedDate,
                start,
                end,
                price,
                "CONFIRMED",
                paymentMethod
        );
        booking.setCourtObject(currentCourt);
        return booking;
    }
}