package com.example.sportbookingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.activity.BookingDetailActivity;
import com.example.sportbookingapp.model.Booking;

import java.text.DecimalFormat;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    // Constructor đơn giản hóa
    public BookingHistoryAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

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
        holder.tvPrice.setText("Tổng: " + formatter.format(booking.getTotalPrice()) + " VNĐ");

        // Xử lý trạng thái
        String status = booking.getStatus();
        if ("CANCELLED".equals(status)) {
            holder.tvStatus.setText("Đã hủy");
            holder.tvStatus.setTextColor(Color.RED);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_chip_gray);
            if(holder.btnCancel != null) holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setText("Đã xác nhận");
            holder.tvStatus.setTextColor(Color.parseColor("#007BFF"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_blue_light);
            if(holder.btnCancel != null) holder.btnCancel.setVisibility(View.GONE);
        }

        // Logic mở chi tiết (Dùng chung cho cả nút Xem và click item)
        View.OnClickListener detailClickListener = v -> {
            Intent intent = new Intent(context, BookingDetailActivity.class);
            intent.putExtra("bookingId", booking.getId());
            intent.putExtra("courtName", booking.getCourtName());
            intent.putExtra("totalPrice", booking.getTotalPrice());
            // Truyền thêm tọa độ (nếu có) để chỉ đường chính xác
            if (booking.getCourtObject() != null) {
                intent.putExtra("courtAddress", booking.getCourtObject().getAddress());
                intent.putExtra("courtLat", booking.getCourtObject().getLat());
                intent.putExtra("courtLng", booking.getCourtObject().getLng());
            }
            intent.putExtra("date", booking.getDate());
            intent.putExtra("startTime", booking.getStartTime());
            intent.putExtra("endTime", booking.getEndTime());
            intent.putExtra("status", booking.getStatus());
            intent.putExtra("paymentMethod", booking.getPaymentMethod());
            intent.putExtra("courtImage", booking.getCourtImage());

            context.startActivity(intent);
        };

        holder.btnViewDetails.setOnClickListener(detailClickListener);
        holder.itemView.setOnClickListener(detailClickListener);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvDate, tvTime, tvPrice;
        Button btnCancel, btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCourtNameHistory);
            tvStatus = itemView.findViewById(R.id.tvStatusHistory);
            tvDate = itemView.findViewById(R.id.tvDateHistory);
            tvTime = itemView.findViewById(R.id.tvTimeHistory);
            tvPrice = itemView.findViewById(R.id.tvPriceHistory);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}