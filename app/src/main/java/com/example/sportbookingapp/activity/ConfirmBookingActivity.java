package com.example.sportbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
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

public class ConfirmBookingActivity extends AppCompatActivity {

    private TextView tvCourtName, tvCourtAddress, tvDate, tvTime, tvTotalPrice;
    private EditText edtName, edtPhone;
    private RadioGroup rgPayment;
    private Button btnConfirm;
    private ImageView btnBack;

    private Court currentCourt;
    private String selectedDate;
    private ArrayList<String> selectedSlots;
    private double totalPrice;
    private BookingDAO bookingDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        Intent intent = getIntent();
        currentCourt = (Court) intent.getSerializableExtra("court_object");
        selectedDate = intent.getStringExtra("date");
        selectedSlots = intent.getStringArrayListExtra("slots");
        totalPrice = intent.getDoubleExtra("total_price", 0);

        if (currentCourt == null || selectedSlots == null || selectedSlots.isEmpty()) {
            Toast.makeText(this, "Lỗi dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingDAO = new BookingDAO(this);
        initViews();
        displayData();

        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> processBooking());
    }

    private void initViews() {
        tvCourtName = findViewById(R.id.tvCourtNameConfirm);
        tvCourtAddress = findViewById(R.id.tvCourtAddressConfirm);
        tvDate = findViewById(R.id.tvDateConfirm);
        tvTime = findViewById(R.id.tvTimeConfirm);
        tvTotalPrice = findViewById(R.id.tvTotalPriceConfirm);

        edtName = findViewById(R.id.edtUserName);
        edtPhone = findViewById(R.id.edtUserPhone);
        rgPayment = findViewById(R.id.radioGroupPayment);

        btnConfirm = findViewById(R.id.btnFinalConfirm);
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

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvTotalPrice.setText(formatter.format(totalPrice) + " VNĐ");
    }

    private void processBooking() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = "Tiền mặt";
        int selectedId = rgPayment.getCheckedRadioButtonId();
        if (selectedId == R.id.rbWallet) paymentMethod = "Ví điện tử";
        else if (selectedId == R.id.rbCredit) paymentMethod = "Thẻ tín dụng";

        int userId = 1;

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
                    currentCourt.getPrice(),
                    "CONFIRMED",
                    paymentMethod
            );
            bookingDAO.addBooking(booking);
        }

        Toast.makeText(this, "Đặt sân thành công!", Toast.LENGTH_LONG).show();

        // --- LOGIC CHUYỂN HƯỚNG THÔNG MINH ---

        // 1. Quay về MainActivity trước để xóa hết stack (Booking, Confirm)
        Intent intentMain = new Intent(this, MainActivity.class);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentMain);

        // 2. Sau đó mở HistoryActivity đè lên
        // Như vậy khi user bấm Back ở trang History -> sẽ về Trang chủ (Main)
        Intent intentHistory = new Intent(this, HistoryActivity.class);
        startActivity(intentHistory);

        finish();
    }
}