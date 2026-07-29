package com.example.myapplication.manager;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.entity.Notification;
import com.example.myapplication.data.repository.NotificationRepository;

import java.util.List;

public class NotificationManager {
    private final NotificationRepository notificationRepository;

    public NotificationManager(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        notificationRepository = new NotificationRepository(db.notificationDAO());
    }

    public long insert(Notification notification) {
        return notificationRepository.insert(notification);
    }

    public void insertAll(List<Notification> notificationList) {
        notificationRepository.insertAll(notificationList);
    }

    public void update(Notification notification) {
        notificationRepository.update(notification);
    }

    public LiveData<List<Notification>> getAll(int userId) {
        return notificationRepository.getAll(userId);
    }

    public LiveData<Integer> getUnreadCount(int userId) {
        return notificationRepository.getUnreadCount(userId);
    }

    public void markAllAsRead(int userId) {
        notificationRepository.markAllAsRead(userId);
    }

    public void markAsRead(int notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
}