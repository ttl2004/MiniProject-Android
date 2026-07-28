package com.example.myapplication.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.dao.NotificationDAO;
import com.example.myapplication.data.entity.Notification;

import java.util.List;

public class NotificationRepository {
    private final NotificationDAO notificationDAO;

    public NotificationRepository(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public long insert(Notification notification) {
        return notificationDAO.insert(notification);
    }

    public void insertAll(List<Notification> notifications) {
        notificationDAO.insertAll(notifications);
    }

    public void update(Notification notification) {
        notificationDAO.update(notification);
    }

    public LiveData<List<Notification>> getAll() {
        return notificationDAO.getAll();
    }

    public LiveData<Integer> getUnreadCount() {
        return notificationDAO.getUnreadCount();
    }

    public void markAllAsRead() {
        notificationDAO.markAllAsRead();
    }

    public void markAsRead(int notificationId) {
        notificationDAO.markAsRead(notificationId);
    }
}