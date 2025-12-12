package com.example.sportbookingapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.adapter.BookingHistoryAdapter;
import com.example.sportbookingapp.database.BookingDAO;
import com.example.sportbookingapp.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView rcvHistory;
    private TextView tabUpcoming, tabHistory;
    private BookingDAO bookingDAO;
    private List<Booking> allBookings;
    private BookingHistoryAdapter adapter;
    private boolean isShowingUpcoming = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        bookingDAO = new BookingDAO(getContext());
        
        tabUpcoming.setOnClickListener(v -> switchTab(true));
        tabHistory.setOnClickListener(v -> switchTab(false));
    }

    private void initViews(View view) {
        rcvHistory = view.findViewById(R.id.rcvBookingHistory);
        tabUpcoming = view.findViewById(R.id.tabUpcoming);
        tabHistory = view.findViewById(R.id.tabHistory);
        rcvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        // Lấy userId=1 (giả lập)
        allBookings = bookingDAO.getBookingsByUser(1);
        filterList();
    }

    private void switchTab(boolean showUpcoming) {
        isShowingUpcoming = showUpcoming;
        if (showUpcoming) {
            tabUpcoming.setBackgroundResource(R.drawable.bg_card_info_item);
            tabUpcoming.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            tabUpcoming.setTypeface(null, android.graphics.Typeface.BOLD);

            tabHistory.setBackground(null);
            tabHistory.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_dark)); // hoặc #757575
            tabHistory.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            tabHistory.setBackgroundResource(R.drawable.bg_card_info_item);
            tabHistory.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            tabHistory.setTypeface(null, android.graphics.Typeface.BOLD);

            tabUpcoming.setBackground(null);
            tabUpcoming.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_dark));
            tabUpcoming.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        filterList();
    }

    private void filterList() {
        if (allBookings == null) return;
        List<Booking> filtered = new ArrayList<>();

        for (Booking b : allBookings) {
            boolean isCompleted = "COMPLETED".equals(b.getStatus()) || "CANCELLED".equals(b.getStatus());
            if (isShowingUpcoming && !isCompleted) {
                filtered.add(b);
            } else if (!isShowingUpcoming && isCompleted) {
                filtered.add(b);
            }
        }

        adapter = new BookingHistoryAdapter(getContext(), filtered);
        rcvHistory.setAdapter(adapter);
    }
}