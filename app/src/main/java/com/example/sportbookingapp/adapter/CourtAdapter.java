package com.example.sportbookingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportbookingapp.R;
import com.example.sportbookingapp.model.Court;

import java.util.List;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.CourtViewHolder> {

    private Context context;
    private List<Court> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Court court);
    }

    public CourtAdapter(Context context, List<Court> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public void updateList(List<Court> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // SỬA: Dùng layout item_court.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_court, parent, false);
        return new CourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {
        Court court = list.get(position);

        // 1. Set text
        holder.tvName.setText(court.getName());
        holder.tvAddress.setText(court.getAddress());
        holder.tvPrice.setText(String.format("%,.0f đ/h", court.getPrice()));

        // 2. Xử lý Ảnh (Lấy từ tên file trong Database)
        // VD: DB lưu "san_bong_1" -> Tìm R.drawable.san_bong_1
        String imgName = court.getImageName();
        int imgResId = context.getResources().getIdentifier(imgName, "drawable", context.getPackageName());

        if (imgResId != 0) {
            holder.img.setImageResource(imgResId);
        } else {
            // Ảnh mặc định nếu không tìm thấy
            holder.img.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // 3. Sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(court);
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    // ViewHolder ánh xạ các View trong item_court.xml
    public static class CourtViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvAddress, tvPrice;

        public CourtViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgCourt);
            tvName = itemView.findViewById(R.id.tvCourtName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}