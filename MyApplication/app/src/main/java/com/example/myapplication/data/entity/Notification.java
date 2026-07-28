package com.example.myapplication.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class Notification {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String message;
    private String createdAt;
    private boolean isRead;

    public Notification(String title, String message, String createdAt, boolean isRead) {
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // --- Getter và Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}