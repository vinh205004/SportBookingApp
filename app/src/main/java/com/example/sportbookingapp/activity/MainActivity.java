package com.example.sportbookingapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.adapter.CourtAdapter;
import com.example.sportbookingapp.database.CourtDAO;
import com.example.sportbookingapp.model.Court;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private MapView mapView;
    private CourtDAO courtDAO;
    private FloatingActionButton fabList;
    private BottomNavigationView bottomNavigationView;
    private EditText edtSearch;
    private ImageView btnClearSearch;
    private ChipGroup chipGroup;
    private RecyclerView rvCourts;
    private CourtAdapter adapter;
    private ImageView btnFilterDistance;

    // Danh sách gốc và marker
    private List<Court> originalCourtList = new ArrayList<>();
    private List<Marker> currentMarkers = new ArrayList<>();

    // Biến lọc
    private String currentSearchKeyword = "";
    private String currentSportFilter = "";
    private double filterDistanceKm = 0; // 0 = Không lọc khoảng cách

    // Biến vị trí
    private LocationManager locationManager;
    private double userLat = 21.0285;
    private double userLng = 105.8542;
    private boolean isLocationFound = false;
    private MyLocationNewOverlay myLocationOverlay;

    // Chế độ xem
    private boolean isMapMode = true;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Cấu hình OSM
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        // 2. Ánh xạ View
        initViews();

        // 3. Setup Map cơ bản
        setupMap();

        // 4. Setup RecyclerView
        rvCourts.setLayoutManager(new LinearLayoutManager(this));

        // 5. Load dữ liệu từ Database
        courtDAO = new CourtDAO(this);
        originalCourtList = courtDAO.getAllCourts();

        // Setup Adapter
        adapter = new CourtAdapter(this, new ArrayList<>(originalCourtList), new CourtAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Court court) {
                // Chuyển màn hình Detail (đã uncomment để bạn dùng)
//                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                intent.putExtra("selected_court", court);
//                startActivity(intent);
            }
        });
        rvCourts.setAdapter(adapter);

        // 6. Xử lý quyền vị trí & Lấy GPS
        checkAndRequestLocationPermission();

        // 7. Vẽ Map lần đầu
        applyFilters();

        // 8. Các sự kiện (Search, Chip, FAB...)
        setupEvents();
    }

    private void initViews() {
        mapView = findViewById(R.id.osmMap);
        fabList = findViewById(R.id.fab_list);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        edtSearch = findViewById(R.id.edt_search);
        btnClearSearch = findViewById(R.id.btn_clear_search);
        chipGroup = findViewById(R.id.chip_group);
        rvCourts = findViewById(R.id.rvCourts);
        btnFilterDistance = findViewById(R.id.btn_filter_distance);
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(userLat, userLng);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(startPoint);
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    // --- PHẦN XỬ LÝ GPS ---

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Xin quyền
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            // Lấy vị trí cuối cùng được lưu
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                userLat = lastKnownLocation.getLatitude();
                userLng = lastKnownLocation.getLongitude();
                isLocationFound = true;
                // Di chuyển map đến vị trí ngay
                mapView.getController().animateTo(new GeoPoint(userLat, userLng));
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);

        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể truy cập GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Cập nhật tọa độ khi người dùng di chuyển
        userLat = location.getLatitude();
        userLng = location.getLongitude();
        isLocationFound = true;

        // Nếu đang bật lọc khoảng cách, thì phải lọc lại danh sách realtime
        if (filterDistanceKm > 0) {
            applyFilters();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
                myLocationOverlay.enableMyLocation();
            } else {
                Toast.makeText(this, "Cần quyền vị trí để tìm sân gần bạn", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- PHẦN LOGIC FILTER ---

    private void showDistancePopup(View view) {
        if (!isLocationFound) {
            Toast.makeText(this, "Đang lấy vị trí...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo PopupMenu gắn vào nút bấm (view)
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_distance, popup.getMenu());

        // Xử lý khi chọn item
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.dist_all) {
                filterDistanceKm = 0;
                Toast.makeText(MainActivity.this, "Hiển thị tất cả sân", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.dist_2km) {
                filterDistanceKm = 2;
                Toast.makeText(MainActivity.this, "Lọc sân dưới 2km", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.dist_5km) {
                filterDistanceKm = 5;
                Toast.makeText(MainActivity.this, "Lọc sân dưới 5km", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.dist_10km) {
                filterDistanceKm = 10;
                Toast.makeText(MainActivity.this, "Lọc sân dưới 10km", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.dist_20km) {
                filterDistanceKm = 20;
                Toast.makeText(MainActivity.this, "Lọc sân dưới 20km", Toast.LENGTH_SHORT).show();
            }

            // Gọi hàm lọc lại danh sách
            applyFilters();
            return true;
        });

        popup.show();
    }

    private void applyFilters() {
        List<Court> filteredList = new ArrayList<>();

        for (Court c : originalCourtList) {
            boolean matchName = true;
            boolean matchType = true;
            boolean matchDistance = true;

            // 1. Lọc tên
            if (!currentSearchKeyword.isEmpty()) {
                String keyword = currentSearchKeyword.toLowerCase();
                String name = c.getName().toLowerCase();
                String address = c.getAddress() != null ? c.getAddress().toLowerCase() : "";
                if (!name.contains(keyword) && !address.contains(keyword)) matchName = false;
            }

            // 2. Lọc loại
            if (!currentSportFilter.isEmpty()) {
                if (c.getType() == null || !c.getType().contains(currentSportFilter)) matchType = false;
            }

            // 3. Lọc khoảng cách (Real Logic)
            if (filterDistanceKm > 0 && isLocationFound) {
                float[] results = new float[1];
                // Tính khoảng cách giữa (User) và (Sân)
                Location.distanceBetween(userLat, userLng, c.getLat(), c.getLng(), results);
                float distanceInMeters = results[0];
                float distanceInKm = distanceInMeters / 1000;

                if (distanceInKm > filterDistanceKm) {
                    matchDistance = false;
                }
            }

            if (matchName && matchType && matchDistance) filteredList.add(c);
        }

        // Tối ưu: Nếu đang lọc khoảng cách, Sắp xếp sân gần nhất lên đầu
        if (filterDistanceKm > 0 && isLocationFound) {
            Collections.sort(filteredList, new Comparator<Court>() {
                @Override
                public int compare(Court c1, Court c2) {
                    float[] res1 = new float[1];
                    float[] res2 = new float[1];
                    Location.distanceBetween(userLat, userLng, c1.getLat(), c1.getLng(), res1);
                    Location.distanceBetween(userLat, userLng, c2.getLat(), c2.getLng(), res2);
                    return Float.compare(res1[0], res2[0]);
                }
            });
        }

        updateMapMarkers(filteredList);
        if (adapter != null) adapter.updateList(filteredList);
    }

    private void updateMapMarkers(List<Court> listToDraw) {
        if (mapView == null) return;

        // Xóa marker cũ (trừ marker vị trí của tôi - MyLocationOverlay)
        mapView.getOverlays().removeAll(currentMarkers);
        currentMarkers.clear();

        for (Court court : listToDraw) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(court.getLat(), court.getLng()));
            marker.setTitle(court.getName());
            marker.setSnippet(String.format("%,.0f đ/h", court.getPrice()));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            marker.setOnMarkerClickListener((m, map) -> {
                m.showInfoWindow();
                return true;
            });

//            marker.setOnInfoWindowClickListener((m, map) -> {
//                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                intent.putExtra("selected_court", court);
//                startActivity(intent);
//            });

            mapView.getOverlays().add(marker);
            currentMarkers.add(marker);
        }

        // Đảm bảo MyLocationOverlay luôn nằm trên cùng
        if(myLocationOverlay != null && !mapView.getOverlays().contains(myLocationOverlay)){
            mapView.getOverlays().add(myLocationOverlay);
        }

        mapView.invalidate();
    }

    private void setupEvents() {
        // Search
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchKeyword = s.toString().trim();
                btnClearSearch.setVisibility(currentSearchKeyword.length() > 0 ? View.VISIBLE : View.GONE);
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> edtSearch.setText(""));

        // Nút Lọc khoảng cách
        btnFilterDistance.setOnClickListener(v -> showDistancePopup(v));

        // Chip Filter
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) currentSportFilter = "";
            else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_all) currentSportFilter = "";
                else if (checkedId == R.id.chip_football) currentSportFilter = "BongDa";
                else if (checkedId == R.id.chip_badminton) currentSportFilter = "CauLong";
                else if (checkedId == R.id.chip_tennis) currentSportFilter = "Tennis";
                else if (checkedId == R.id.chip_basketball) currentSportFilter = "BongRo";
            }
            applyFilters();
        });

        // FAB Toggle List/Map
        fabList.setOnClickListener(v -> {
            if (isMapMode) {
                mapView.setVisibility(View.GONE);
                rvCourts.setVisibility(View.VISIBLE);
                fabList.setImageResource(android.R.drawable.ic_dialog_map);
                isMapMode = false;
            } else {
                mapView.setVisibility(View.VISIBLE);
                rvCourts.setVisibility(View.GONE);
                fabList.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                isMapMode = true;
            }
        });

        // Bottom Nav
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_explore) return true;
            else if (id == R.id.nav_booking) {
//                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
//                startActivity(intent);
//                return true;
            } else if (id == R.id.nav_profile) {
//                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//                startActivity(intent);
//                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
        if (myLocationOverlay != null) myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
        if (myLocationOverlay != null) myLocationOverlay.disableMyLocation();
        // Dừng lấy vị trí để tiết kiệm pin
        if (locationManager != null) locationManager.removeUpdates(this);
    }
}