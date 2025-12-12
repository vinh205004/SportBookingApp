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
    private OnActionClickListener listener;

    // Interface cho sự kiện Hủy
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

        // --- 1. XỬ LÝ TRẠNG THÁI THANH TOÁN (Logic mới) ---
        String method = booking.getPaymentMethod();

        // Mặc định nếu null coi như là COD
        if (method == null || method.equals("COD")) {
            holder.tvPaymentStatus.setText("Chưa thanh toán");
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#F44336")); // Đỏ
        } else {
            // Banking, E-Wallet...
            holder.tvPaymentStatus.setText("Đã thanh toán");
            holder.tvPaymentStatus.setTextColor(Color.parseColor("#4CAF50")); // Xanh lá
        }

        // --- 2. XỬ LÝ TRẠNG THÁI ĐƠN HÀNG ---
        String status = booking.getStatus();
        if ("CANCELLED".equals(status)) {
            holder.tvStatus.setText("Đã hủy");
            holder.tvStatus.setTextColor(Color.RED);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_chip_gray); // Đảm bảo có file drawable này
            holder.btnCancel.setVisibility(View.GONE); // Đã hủy thì ẩn nút hủy đi

            // Nếu đã hủy thì trạng thái thanh toán nên mờ đi hoặc ẩn
            holder.tvPaymentStatus.setText("Đã hủy đơn");
            holder.tvPaymentStatus.setTextColor(Color.GRAY);

        } else {
            holder.tvStatus.setText("Đã xác nhận");
            holder.tvStatus.setTextColor(Color.parseColor("#007BFF"));
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_blue_light); // Đảm bảo có file drawable này
            holder.btnCancel.setVisibility(View.VISIBLE);
        }

        // --- 3. SỰ KIỆN CLICK ---

        // Nút Hủy
        holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(booking));

        // Nút Xem chi tiết
        View.OnClickListener detailClickListener = v -> {
            Intent intent = new Intent(context, BookingDetailActivity.class);
            intent.putExtra("bookingId", booking.getId());
            intent.putExtra("courtName", booking.getCourtName());
            intent.putExtra("totalPrice", booking.getTotalPrice());
            intent.putExtra("date", booking.getDate());
            intent.putExtra("startTime", booking.getStartTime());
            intent.putExtra("endTime", booking.getEndTime());
            intent.putExtra("status", booking.getStatus());

            // Gửi thêm thông tin phụ
            if (booking.getCourtObject() != null) {
                intent.putExtra("courtAddress", booking.getCourtObject().getAddress());
                intent.putExtra("courtType", booking.getCourtObject().getType());
            } else {
                intent.putExtra("courtAddress", "Đang cập nhật");
                intent.putExtra("courtType", "Sân tiêu chuẩn");
            }

            context.startActivity(intent);
        };

        holder.btnViewDetails.setOnClickListener(detailClickListener);
        holder.itemView.setOnClickListener(detailClickListener); // Bấm vào đâu cũng xem chi tiết được
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // --- VIEW HOLDER (Nơi khai báo biến View) ---
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvDate, tvTime, tvPrice;
        TextView tvPaymentStatus;
        Button btnCancel, btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCourtNameHistory);
            tvStatus = itemView.findViewById(R.id.tvStatusHistory);
            tvDate = itemView.findViewById(R.id.tvDateHistory);
            tvTime = itemView.findViewById(R.id.tvTimeHistory);
            tvPrice = itemView.findViewById(R.id.tvPriceHistory);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}