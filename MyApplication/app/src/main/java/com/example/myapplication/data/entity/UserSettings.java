package com.example.myapplication.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user_settings")
public class UserSettings implements Serializable {
    @PrimaryKey
    private int userId;

    private boolean isDarkMode;
    private boolean isReminderEnabled;
    private int reminderHour;
    private int reminderMinute;
    private String reminderNote;

    public UserSettings(int userId) {
        this.userId = userId;
        this.isDarkMode = false;
        this.isReminderEnabled = true;
        this.reminderHour = 20;
        this.reminderMinute = 0;
        this.reminderNote = "Đừng quên ghi chép chi tiêu hôm nay nhé!";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
    }

    public boolean isReminderEnabled() {
        return isReminderEnabled;
    }

    public void setReminderEnabled(boolean reminderEnabled) {
        isReminderEnabled = reminderEnabled;
    }

    public int getReminderHour() {
        return reminderHour;
    }

    public void setReminderHour(int reminderHour) {
        this.reminderHour = reminderHour;
    }

    public int getReminderMinute() {
        return reminderMinute;
    }

    public void setReminderMinute(int reminderMinute) {
        this.reminderMinute = reminderMinute;
    }

    public String getReminderNote() {
        return reminderNote;
    }

    public void setReminderNote(String reminderNote) {
        this.reminderNote = reminderNote;
    }
}