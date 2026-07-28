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

    public LiveData<List<Notification>> getAll() {
        return notificationRepository.getAll();
    }

    public LiveData<Integer> getUnreadCount() {
        return notificationRepository.getUnreadCount();
    }

    public void markAllAsRead() {
        notificationRepository.markAllAsRead();
    }

    public void markAsRead(int notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
}