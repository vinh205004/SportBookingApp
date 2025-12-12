package com.example.sportbookingapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.model.Court;

import java.text.DecimalFormat;
import java.util.Locale;

public class CourtDetailActivity extends AppCompatActivity {

    // Khai báo các view theo layout mới
    private TextView tvName, tvAddress, tvType, tvPrice, tvOpenTime, tvRating, tvDesc, tvPriceBottom;
    private TextView tvDistance;
    private ImageView imgCourt;
    private ImageButton btnBack, btnFavorite;
    private Button btnBookNow, btnDirections;

    private Court currentCourt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_detail);

        // 1. Nhận dữ liệu từ Intent
        currentCourt = (Court) getIntent().getSerializableExtra("court_object");

        if (currentCourt == null) {
            currentCourt = new Court(1, "Sân bóng Thanh Xuân", "123 Nguyễn Trãi, Thanh Xuân", "Sân 7 người", 150000, "san_bong_1", 4.5, "Wifi,Parking", 0, 0, "Sân cỏ nhân tạo tiêu chuẩn FIFA, đèn chiếu sáng tốt.", "06:00", "23:00");
        }

        initViews();
        setupData();
        setupEvents();
        calculateDistance();
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
        tvDistance = findViewById(R.id.tvDistance); // Mới thêm

        imgCourt = findViewById(R.id.imgCourtDetail);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnBookNow = findViewById(R.id.btnBookNow);
        btnDirections = findViewById(R.id.btnDirections);
    }

    private void setupData() {
        if (currentCourt == null) return;

        tvName.setText(currentCourt.getName());
        tvAddress.setText(currentCourt.getAddress());
        tvType.setText(currentCourt.getType());

        String priceStr = formatMoney(currentCourt.getPrice());
        tvPrice.setText(priceStr.replace("đ", "K"));
        tvPriceBottom.setText(priceStr + "/giờ");

        tvOpenTime.setText(currentCourt.getOpenTime() + " - " + currentCourt.getCloseTime());
        tvRating.setText(String.valueOf(currentCourt.getRating()));
        tvDesc.setText(currentCourt.getDescription());

        int resId = getResources().getIdentifier(currentCourt.getImageName(), "drawable", getPackageName());
        if (resId != 0) {
            imgCourt.setImageResource(resId);
        } else {
            imgCourt.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    private void calculateDistance() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            
            if (lastKnownLocation != null && currentCourt.getLat() != 0) {
                float[] results = new float[1];
                Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 
                        currentCourt.getLat(), currentCourt.getLng(), results);
                float distanceKm = results[0] / 1000;
                tvDistance.setText(String.format(Locale.getDefault(), "Cách bạn: %.1f km", distanceKm));
                tvDistance.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

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

        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(CourtDetailActivity.this, BookingActivity.class);
            intent.putExtra("court_object", currentCourt);
            startActivity(intent);
        });

        // Cập nhật logic chỉ đường để không bắt buộc Google Maps
        btnDirections.setOnClickListener(v -> {
            if (currentCourt.getLat() == 0 && currentCourt.getLng() == 0) {
                Toast.makeText(this, "Chưa có tọa độ sân!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Dùng geo: URI scheme chuẩn thay vì google.navigation
            String uri = String.format(Locale.US, "geo:%f,%f?q=%f,%f(%s)", 
                    currentCourt.getLat(), currentCourt.getLng(), 
                    currentCourt.getLat(), currentCourt.getLng(), 
                    Uri.encode(currentCourt.getName()));
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Không tìm thấy ứng dụng bản đồ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatMoney(double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(price) + "đ";
    }
}