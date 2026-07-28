package com.example.myapplication.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.data.entity.Notification;


import java.util.List;

@Dao
public interface NotificationDAO {

    @Insert
    long insert(Notification notification);

    @Insert
    void insertAll(List<Notification> notificationList);

    @Update
    void update(Notification notification);

    // Lấy toàn bộ danh sách thông báo (sắp xếp mới nhất lên đầu)
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    LiveData<List<Notification>> getAll();

    // Lấy số lượng thông báo CHƯA ĐỌC để hiển thị trên badge chuông
    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    LiveData<Integer> getUnreadCount();

    // Đánh dấu TẤT CẢ thông báo là ĐÃ ĐỌC
    @Query("UPDATE notifications SET isRead = 1 WHERE isRead = 0")
    void markAllAsRead();

    // Đánh dấu 1 thông báo cụ thể là ĐÃ ĐỌC
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    void markAsRead(int notificationId);
}