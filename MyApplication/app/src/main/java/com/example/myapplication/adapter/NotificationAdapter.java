package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.entity.Notification;
import com.example.myapplication.databinding.ItemNotificationBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> list = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    public NotificationAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<Notification> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification item = list.get(position);

        // Gán dữ liệu vào các TextViews
        holder.binding.tvNotificationTitle.setText(item.getTitle());
        holder.binding.tvNotificationMessage.setText(item.getMessage());
        holder.binding.tvNotificationTime.setText(item.getCreatedAt());

        // Xử lý trạng thái ĐÃ ĐỌC / CHƯA ĐỌC
        if (item.isRead()) {
            // Dùng INVISIBLE để giữ nguyên khoảng cách lề
            holder.binding.viewUnreadBadge.setVisibility(View.INVISIBLE);

            // Làm mờ nhẹ tiêu đề cho thông báo đã đọc
            holder.binding.tvNotificationTitle.setAlpha(0.6f);
        } else {
            holder.binding.viewUnreadBadge.setVisibility(View.VISIBLE);
            holder.binding.tvNotificationTitle.setAlpha(1.0f);
        }

        // Bắt sự kiện click
        holder.binding.cardNotification.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}