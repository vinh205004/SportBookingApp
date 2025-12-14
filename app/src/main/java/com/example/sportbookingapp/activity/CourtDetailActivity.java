package com.example.sportbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.model.Court;

import java.text.DecimalFormat;

public class CourtDetailActivity extends AppCompatActivity {

    // Khai báo các view theo layout mới
    private TextView tvName, tvAddress, tvType, tvPrice, tvOpenTime, tvRating, tvDesc, tvPriceBottom;
    private ImageView imgCourt;
    private ImageButton btnBack, btnFavorite;
    private Button btnBookNow;

    private Court currentCourt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_detail);

        // 1. Nhận dữ liệu từ Intent
        currentCourt = (Court) getIntent().getSerializableExtra("court_object");

        // tạo dữ liệu giả
        if (currentCourt == null) {
            currentCourt = new Court(1, "Sân bóng Thanh Xuân", "123 Nguyễn Trãi, Thanh Xuân", "Sân 7 người", 150000, "san_bong_1", 4.5, "Wifi,Parking", 0, 0, "Sân cỏ nhân tạo tiêu chuẩn FIFA, đèn chiếu sáng tốt.", "06:00", "23:00");
        }

        initViews();
        setupData();
        setupEvents();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvCourtNameDetail);
        tvAddress = findViewById(R.id.tvCourtAddressDetail);
        tvType = findViewById(R.id.tvCourtTypeDetail);
        tvPrice = findViewById(R.id.tvPriceDetail);
        tvOpenTime = findViewById(R.id.tvOpenTimeDetail);
        tvRating = findViewById(R.id.tvRatingDetail);
        tvDesc = findViewById(R.id.tvDescriptionDetail);
        tvPriceBottom = findViewById(R.id.tvPriceBottom);

        imgCourt = findViewById(R.id.imgCourtDetail);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnBookNow = findViewById(R.id.btnBookNow);
    }

    private void setupData() {
        if (currentCourt == null) return;

        tvName.setText(currentCourt.getName());
        tvAddress.setText(currentCourt.getAddress());
        tvType.setText(currentCourt.getType());

        // Format tiền tệ cho đẹp (VD: 150.000đ)
        String priceStr = formatMoney(currentCourt.getPrice());
        tvPrice.setText(priceStr.replace("đ", "K")); // Trên grid để ngắn gọn: "150K"
        tvPriceBottom.setText(priceStr + "/giờ");   // Dưới đáy: "150.000đ/giờ"

        tvOpenTime.setText(currentCourt.getOpenTime() + " - " + currentCourt.getCloseTime());
        tvRating.setText(String.valueOf(currentCourt.getRating()));
        tvDesc.setText(currentCourt.getDescription());

        // Load ảnh từ Drawable dựa vào tên file (imageName)
        int resId = getResources().getIdentifier(currentCourt.getImageName(), "drawable", getPackageName());
        if (resId != 0) {
            imgCourt.setImageResource(resId);
        } else {
            imgCourt.setImageResource(R.drawable.ic_launcher_background); // Ảnh mặc định nếu lỗi
        }
    }

    private void setupEvents() {
        // Nút Back
        btnBack.setOnClickListener(v -> finish()

        );

        // Nút Yêu thích (Giả lập toggle)
        btnFavorite.setOnClickListener(v -> {
            boolean isSelected = !v.isSelected();
            v.setSelected(isSelected);
            if (isSelected) {
                ((ImageButton)v).setImageResource(android.R.drawable.btn_star_big_on);
                Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                ((ImageButton)v).setImageResource(android.R.drawable.btn_star_big_off);
            }
        });

        // Nút ĐẶT SÂN -> Chuyển sang màn hình chọn lịch (BookingActivity)
        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(CourtDetailActivity.this, BookingActivity.class);
            // Truyền tiếp object Court sang màn hình đặt lịch để biết đang đặt sân nào
            intent.putExtra("court_object", currentCourt);
            startActivity(intent);
        });
    }

    private String formatMoney(double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(price) + "đ";
    }
}