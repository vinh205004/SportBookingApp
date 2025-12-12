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
import android.widget.Toast;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.activity.CourtDetailActivity;
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

public class ExploreFragment extends Fragment implements LocationListener {

    private MapView mapView;
    private CourtDAO courtDAO;
    private FloatingActionButton fabList;
    private EditText edtSearch;
    private ImageView btnClearSearch;
    private ChipGroup chipGroup;
    private RecyclerView rvCourts;
    private CourtAdapter adapter;
    private ImageView btnFilterDistance;

    private List<Court> originalCourtList = new ArrayList<>();
    private List<Marker> currentMarkers = new ArrayList<>();

    private String currentSearchKeyword = "";
    private String currentSportFilter = "";
    private double filterDistanceKm = 0;

    private LocationManager locationManager;
    private double userLat = 21.0285;
    private double userLng = 105.8542;
    private boolean isLocationFound = false;
    private MyLocationNewOverlay myLocationOverlay;
    private boolean isMapMode = true;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Cấu hình OSM (Sử dụng SharedPreferences của Activity để tránh Deprecated)
        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());

        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupMap();
        
        rvCourts.setLayoutManager(new LinearLayoutManager(getContext()));
        courtDAO = new CourtDAO(getContext());
        originalCourtList = courtDAO.getAllCourts();
        
        adapter = new CourtAdapter(getContext(), new ArrayList<>(originalCourtList), this::goToDetail);
        rvCourts.setAdapter(adapter);

        checkAndRequestLocationPermission();
        applyFilters();
        setupEvents();
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

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

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
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                userLat = lastKnownLocation.getLatitude();
                userLng = lastKnownLocation.getLongitude();
                isLocationFound = true;
                mapView.getController().animateTo(new GeoPoint(userLat, userLng));
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        userLat = location.getLatitude();
        userLng = location.getLongitude();
        isLocationFound = true;
        if (filterDistanceKm > 0) applyFilters();
    }

    private void applyFilters() {
        List<Court> filteredList = new ArrayList<>();
        for (Court c : originalCourtList) {
            boolean matchName = true;
            boolean matchType = true;
            boolean matchDistance = true;

            if (!currentSearchKeyword.isEmpty()) {
                String keyword = currentSearchKeyword.toLowerCase();
                String name = c.getName().toLowerCase();
                String address = c.getAddress() != null ? c.getAddress().toLowerCase() : "";
                if (!name.contains(keyword) && !address.contains(keyword)) matchName = false;
            }

            if (!currentSportFilter.isEmpty()) {
                if (c.getType() == null || !c.getType().contains(currentSportFilter)) matchType = false;
            }

            if (filterDistanceKm > 0 && isLocationFound) {
                float[] results = new float[1];
                Location.distanceBetween(userLat, userLng, c.getLat(), c.getLng(), results);
                float distanceInKm = results[0] / 1000;
                if (distanceInKm > filterDistanceKm) matchDistance = false;
            }

            if (matchName && matchType && matchDistance) filteredList.add(c);
        }

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
            marker.setSnippet(getString(R.string.marker_snippet_format, court.getPrice()));
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
        if (myLocationOverlay != null && !mapView.getOverlays().contains(myLocationOverlay)) {
            mapView.getOverlays().add(myLocationOverlay);
        }
        mapView.invalidate();
    }

    private void goToDetail(Court court) {
        Intent intent = new Intent(getContext(), CourtDetailActivity.class);
        intent.putExtra("court_object", court);
        startActivity(intent);
    }

    private void setupEvents() {
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
        
        btnFilterDistance.setOnClickListener(this::showDistancePopup);

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

    private void showDistancePopup(View view) {
        if (!isLocationFound) {
            Toast.makeText(getContext(), getString(R.string.fetching_location), Toast.LENGTH_SHORT).show();
            return;
        }
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.menu_distance, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.dist_all) filterDistanceKm = 0;
            else if (id == R.id.dist_2km) filterDistanceKm = 2;
            else if (id == R.id.dist_5km) filterDistanceKm = 5;
            else if (id == R.id.dist_10km) filterDistanceKm = 10;
            else if (id == R.id.dist_20km) filterDistanceKm = 20;
            
            Toast.makeText(getContext(), getString(R.string.filtering), Toast.LENGTH_SHORT).show();
            applyFilters();
            return true;
        });
        popup.show();
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