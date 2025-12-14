package com.example.sportbookingapp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.activity.CourtDetailActivity; //
import com.example.sportbookingapp.adapter.CourtAdapter;
import com.example.sportbookingapp.database.CourtDAO;
import com.example.sportbookingapp.model.Court;
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
import java.util.List;

public class HomeFragment extends Fragment implements LocationListener {

    private MapView mapView;
    private CourtDAO courtDAO;
    private FloatingActionButton fabList;
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
    private double userLat = 21.0285; // Mặc định Hà Nội
    private double userLng = 105.8542;
    private boolean isLocationFound = false;
    private MyLocationNewOverlay myLocationOverlay;

    // Chế độ xem (List hoặc Map)
    private boolean isMapMode = true;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Cấu hình OSM
        Configuration.getInstance().load(requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext()));
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        // Nạp giao diện fragment_home
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 2. Ánh xạ View
        initViews(view);

        // 3. Setup Map cơ bản
        setupMap();

        // 4. Setup RecyclerView
        rvCourts.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 5. Load dữ liệu từ Database
        courtDAO = new CourtDAO(requireContext());
        originalCourtList = courtDAO.getAllCourts();

        // Setup Adapter
        adapter = new CourtAdapter(requireContext(), new ArrayList<>(originalCourtList), new CourtAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Court court) {
                goToDetail(court);
            }
        });
        rvCourts.setAdapter(adapter);

        // 6. Xử lý quyền vị trí & Lấy GPS
        checkAndRequestLocationPermission();

        // 7. Lọc
        applyFilters();

        // 8. Các sự kiện (Search, Chip, FAB...)
        setupEvents();

        return view;
    }

    private void initViews(View view) {
        mapView = view.findViewById(R.id.osmMap);
        fabList = view.findViewById(R.id.fab_list);
        edtSearch = view.findViewById(R.id.edt_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);
        chipGroup = view.findViewById(R.id.chip_group);
        rvCourts = view.findViewById(R.id.rvCourts);
        btnFilterDistance = view.findViewById(R.id.btn_filter_distance);
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(userLat, userLng);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(startPoint);

        // Overlay hiển thị vị trí của tôi
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    // --- PHẦN XỬ LÝ GPS ---

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        try {
            locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

            // Lấy vị trí cuối cùng được lưu
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                userLat = lastKnownLocation.getLatitude();
                userLng = lastKnownLocation.getLongitude();
                isLocationFound = true;
                mapView.getController().animateTo(new GeoPoint(userLat, userLng));
            }

            // Yêu cầu cập nhật vị trí
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);

        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Không thể truy cập GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        userLat = location.getLatitude();
        userLng = location.getLongitude();
        isLocationFound = true;

        if (filterDistanceKm > 0) {
            applyFilters();
        }
    }

    // Nhận kết quả xin quyền trong Fragment
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
                if (myLocationOverlay != null) myLocationOverlay.enableMyLocation();
            } else {
                Toast.makeText(getContext(), "Cần quyền vị trí để tìm sân gần bạn", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- PHẦN LOGIC FILTER ---

    private void showDistancePopup(View view) {
        if (!isLocationFound) {
            Toast.makeText(getContext(), "Đang lấy vị trí...", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupMenu popup = new PopupMenu(requireContext(), view);
        popup.getMenuInflater().inflate(R.menu.menu_distance, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.dist_all) filterDistanceKm = 0;
            else if (id == R.id.dist_2km) filterDistanceKm = 2;
            else if (id == R.id.dist_5km) filterDistanceKm = 5;
            else if (id == R.id.dist_10km) filterDistanceKm = 10;
            else if (id == R.id.dist_20km) filterDistanceKm = 20;

            String msg = filterDistanceKm == 0 ? "Hiển thị tất cả" : "Lọc dưới " + filterDistanceKm + "km";
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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

            // 3. Lọc khoảng cách
            if (filterDistanceKm > 0 && isLocationFound) {
                float[] results = new float[1];
                Location.distanceBetween(userLat, userLng, c.getLat(), c.getLng(), results);
                float distanceInKm = results[0] / 1000;

                if (distanceInKm > filterDistanceKm) {
                    matchDistance = false;
                }
            }

            if (matchName && matchType && matchDistance) filteredList.add(c);
        }

        // Sắp xếp
        if (filterDistanceKm > 0 && isLocationFound) {
            Collections.sort(filteredList, (c1, c2) -> {
                float[] res1 = new float[1];
                float[] res2 = new float[1];
                Location.distanceBetween(userLat, userLng, c1.getLat(), c1.getLng(), res1);
                Location.distanceBetween(userLat, userLng, c2.getLat(), c2.getLng(), res2);
                return Float.compare(res1[0], res2[0]);
            });
        }

        updateMapMarkers(filteredList);
        if (adapter != null) adapter.updateList(filteredList);
    }

    private void updateMapMarkers(List<Court> listToDraw) {
        if (mapView == null) return;

        mapView.getOverlays().removeAll(currentMarkers);
        currentMarkers.clear();

        for (Court court : listToDraw) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(court.getLat(), court.getLng()));
            marker.setTitle(court.getName());
            marker.setSnippet(String.format("%,.0f đ/h\n(Bấm lần nữa để xem)", court.getPrice()));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            marker.setOnMarkerClickListener((m, map) -> {
                if (m.isInfoWindowShown()) {
                    goToDetail(court);
                    m.closeInfoWindow();
                } else {
                    m.showInfoWindow();
                }
                return true;
            });

            mapView.getOverlays().add(marker);
            currentMarkers.add(marker);
        }

        if(myLocationOverlay != null && !mapView.getOverlays().contains(myLocationOverlay)){
            mapView.getOverlays().add(myLocationOverlay);
        }

        mapView.invalidate();
    }

    private void goToDetail(Court court) {
        Intent intent = new Intent(requireContext(), CourtDetailActivity.class);
        intent.putExtra("court_object", court); //
        startActivity(intent);
    }

    private void setupEvents() {
        // Search
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchKeyword = s.toString().trim();
                btnClearSearch.setVisibility(currentSearchKeyword.length() > 0 ? View.VISIBLE : View.GONE);
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> edtSearch.setText(""));

        // Lọc khoảng cách
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

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
        if (myLocationOverlay != null) myLocationOverlay.enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
        if (myLocationOverlay != null) myLocationOverlay.disableMyLocation();
        if (locationManager != null) locationManager.removeUpdates(this);
    }
}