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

        // 1. Xử lý trạng thái Đơn hàng (CONFIRMED / CANCELLED)
        String status = booking.getStatus();
        if ("CANCELLED".equals(status)) {
            holder.tvStatus.setText("Đã hủy");
            holder.tvStatus.setTextColor(Color.RED);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_chip_gray);
            holder.btnCancel.setVisibility(View.GONE);

            // Đã hủy thì ẩn luôn trạng thái thanh toán cho đỡ rối
            holder.tvPaymentStatus.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setText("Đã xác nhận");
            holder.tvStatus.setTextColor(Color.parseColor("#007BFF"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_blue_light);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.tvPaymentStatus.setVisibility(View.VISIBLE);

            // 2. Xử lý trạng thái Thanh toán (Dựa trên Payment Method)
            // Trong ConfirmBookingActivity chúng ta lưu là "Tiền mặt", "Ví điện tử", ...
            String paymentMethod = booking.getPaymentMethod();

            if (paymentMethod != null && paymentMethod.equals("Tiền mặt")) {
                holder.tvPaymentStatus.setText("Chưa thanh toán (Thu tại sân)");
                holder.tvPaymentStatus.setTextColor(Color.parseColor("#FF9800")); // Màu Cam
            } else {
                // Ví điện tử / Thẻ tín dụng -> Coi như đã thanh toán
                holder.tvPaymentStatus.setText("Đã thanh toán (" + paymentMethod + ")");
                holder.tvPaymentStatus.setTextColor(Color.parseColor("#4CAF50")); // Màu Xanh lá
            }
        }

        holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvDate, tvTime, tvPrice, tvPaymentStatus; // Thêm tvPaymentStatus
        Button btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCourtNameHistory);
            tvStatus = itemView.findViewById(R.id.tvStatusHistory);
            tvDate = itemView.findViewById(R.id.tvDateHistory);
            tvTime = itemView.findViewById(R.id.tvTimeHistory);
            tvPrice = itemView.findViewById(R.id.tvPriceHistory);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus); // Ánh xạ view mới
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}