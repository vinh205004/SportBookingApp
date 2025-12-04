package com.example.sportbookingapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.model.TimeSlot;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private List<TimeSlot> list;
    private OnSlotClickListener listener;

    // Interface cập nhật: Có tham số TimeSlot để sửa lỗi Lambda 0 params found 1
    public interface OnSlotClickListener {
        void onSlotClick(TimeSlot slot);
    }

    public TimeSlotAdapter(List<TimeSlot> list, OnSlotClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeSlot slot = list.get(position);
        holder.tvTime.setText(slot.getTimeLabel()); // Dùng getTimeLabel() mới

        if (slot.isBooked()) {
            holder.tvTime.setBackgroundColor(Color.GRAY);
            holder.tvTime.setTextColor(Color.WHITE);
            holder.itemView.setEnabled(false);
        } else if (slot.isSelected()) {
            holder.tvTime.setBackgroundColor(Color.parseColor("#2196F3"));
            holder.tvTime.setTextColor(Color.WHITE);
            holder.itemView.setEnabled(true);
        } else {
            holder.tvTime.setBackgroundResource(android.R.drawable.btn_default);
            holder.tvTime.setTextColor(Color.BLACK);
            holder.itemView.setEnabled(true);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!slot.isBooked()) {
                slot.setSelected(!slot.isSelected());
                notifyItemChanged(position);
                // Truyền slot ra ngoài Activity
                listener.onSlotClick(slot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTimeSlot);
        }
    }
}