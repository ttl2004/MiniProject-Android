package com.example.myapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.myapplication.data.entity.UserSettings;
import com.example.myapplication.manager.ReminderScheduler;
import com.example.myapplication.manager.UserSettingsManager;

import java.util.concurrent.Executors;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            // Lấy userId của người dùng hiện tại từ SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int currentUserId = prefs.getInt("USER_ID", -1);

            if (currentUserId == -1) return; // Chưa đăng nhập thì bỏ qua

            // Truy vấn Database lấy cấu hình cài đặt của user đó
            Executors.newSingleThreadExecutor().execute(() -> {
                UserSettingsManager manager = new UserSettingsManager(context);
                UserSettings settings = manager.getSettingsByUserIdSync(currentUserId);

                // Nếu user có bật nhắc nhở, đăng ký lại AlarmManager
                if (settings != null && settings.isReminderEnabled()) {
                    ReminderScheduler.scheduleDailyReminder(
                            context,
                            currentUserId,
                            settings.getReminderHour(),
                            settings.getReminderMinute(),
                            settings.getReminderNote()
                    );
                }
            });
        }
    }
}