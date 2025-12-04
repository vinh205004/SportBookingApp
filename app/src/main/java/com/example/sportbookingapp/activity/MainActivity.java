package com.example.sportbookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.model.Court;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGoToDetail = findViewById(R.id.btnGoToDetail);

        btnGoToDetail.setOnClickListener(v -> {
            // 1. Tạo dữ liệu giả (Mock Data) để gửi sang trang chi tiết
            // Constructor: id, name, address, type, price, image, rating, facilities, lat, lng, desc, open, close
            Court sampleCourt = new Court(
                    1,
                    "Sân bóng Thanh Xuân",
                    "123 Nguyễn Trãi, Thanh Xuân, Hà Nội",
                    "Sân 7 người",
                    150000,
                    "san_bong_1", // Đảm bảo bạn có file ảnh này hoặc dùng ic_launcher_background
                    4.5,
                    "Wifi,Parking,Canteen",
                    21.0, 105.8,
                    "Sân cỏ nhân tạo chất lượng cao, đèn chiếu sáng tốt, phục vụ nước uống miễn phí.",
                    "06:00",
                    "23:00"
            );

            // 2. Chuyển màn hình và gửi kèm dữ liệu
            Intent intent = new Intent(MainActivity.this, CourtDetailActivity.class);
            intent.putExtra("court_object", sampleCourt);
            startActivity(intent);
        });
    }
}