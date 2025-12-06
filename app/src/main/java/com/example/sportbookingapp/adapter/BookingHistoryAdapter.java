package com.example.sportbookingapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.model.Booking;

import java.text.DecimalFormat;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onCancelClick(Booking booking);
    }

    public BookingHistoryAdapter(Context context, List<Booking> bookingList, OnActionClickListener listener) {
        this.context = context;
        this.bookingList = bookingList;
        this.listener = listener;
    }

    // Hàm cập nhật lại list khi có thay đổi
    public void updateList(List<Booking> newList) {
        this.bookingList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvName.setText(booking.getCourtName());
        holder.tvDate.setText("Ngày: " + booking.getDate());
        holder.tvTime.setText("Giờ: " + booking.getStartTime() + " - " + booking.getEndTime());

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        holder.tvPrice.setText(formatter.format(booking.getTotalPrice()) + " VNĐ");

        // Xử lý trạng thái
        String status = booking.getStatus(); // "CONFIRMED", "CANCELLED"
        if ("CANCELLED".equals(status)) {
            holder.tvStatus.setText("Đã hủy");
            holder.tvStatus.setTextColor(Color.RED);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_chip_gray); // Nền xám
            holder.btnCancel.setVisibility(View.GONE); // Đã hủy thì ẩn nút hủy
        } else {
            holder.tvStatus.setText("Đã xác nhận");
            holder.tvStatus.setTextColor(Color.parseColor("#007BFF")); // Xanh
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_blue_light);
            holder.btnCancel.setVisibility(View.VISIBLE);
        }

        holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvDate, tvTime, tvPrice;
        Button btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCourtNameHistory);
            tvStatus = itemView.findViewById(R.id.tvStatusHistory);
            tvDate = itemView.findViewById(R.id.tvDateHistory);
            tvTime = itemView.findViewById(R.id.tvTimeHistory);
            tvPrice = itemView.findViewById(R.id.tvPriceHistory);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}