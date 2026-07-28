package com.example.myapplication.activity.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.NotificationAdapter;
import com.example.myapplication.data.entity.Notification;
import com.example.myapplication.databinding.ActivityNotificationBinding;
import com.example.myapplication.manager.NotificationManager;

import java.util.concurrent.Executors;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationAdapter adapter;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        notificationManager = new NotificationManager(this);

        setupRecyclerView();
        setupListeners();
        observeNotifications();
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(notification -> {
            // Khi người dùng bấm vào 1 dòng thông báo chưa đọc -> Cập nhật thành Đã đọc
            if (!notification.isRead()) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    notificationManager.markAsRead(notification.getId());
                });
            }
        });

        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);
    }

    private void setupListeners() {
        // Nút Back
        binding.btnBack.setOnClickListener(v -> finish());

        // Nút Đánh dấu tất cả đã đọc
        binding.btnMarkAllRead.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                notificationManager.markAllAsRead();
            });
            Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeNotifications() {
        // Lắng nghe dữ liệu LiveData tự động cập nhật từ Room DB
        notificationManager.getAll().observe(this, notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                binding.layoutEmptyNotifications.setVisibility(View.VISIBLE);
                binding.rvNotifications.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyNotifications.setVisibility(View.GONE);
                binding.rvNotifications.setVisibility(View.VISIBLE);
                adapter.setData(notifications);
            }
        });
    }
}