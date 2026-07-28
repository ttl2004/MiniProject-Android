package com.example.myapplication.receiver;

import android.app.NotificationChannel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;
import com.example.myapplication.data.entity.Notification;
import com.example.myapplication.manager.NotificationManager;
import com.example.myapplication.manager.ReminderScheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "DAILY_REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = intent.getStringExtra("EXTRA_NOTE");
        if (content == null || content.isEmpty()) {
            content = "Đừng quên ghi chép chi tiêu hôm nay nhé!";
        }

        String title = "Nhắc nhở chi tiêu";
        String currentTime = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault()).format(new Date());

        // 1. LƯU VÀO ROOM DATABASE
        NotificationManager appNotificationManager = new NotificationManager(context);
        appNotificationManager.insert(new Notification(
                title,
                content,
                currentTime,
                false
        ));

        // 2. HIỂN THỊ THÔNG BÁO THANH TRẠNG THÁI
        android.app.NotificationManager systemNotificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Nhắc nhở hằng ngày",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kênh thông báo nhắc nhở ghi chép chi tiêu");
            if (systemNotificationManager != null) {
                systemNotificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_calendar_clock)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (systemNotificationManager != null) {
            systemNotificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }

        // 3. TỰ ĐỘNG HẸN LỊCH CHO NGÀY HÔM SAU
        // Đọc lại chính xác GIỜ và PHÚT đã được hẹn từ trước
        int originalHour = intent.getIntExtra("EXTRA_HOUR", -1);
        int originalMinute = intent.getIntExtra("EXTRA_MINUTE", -1);

        if (originalHour != -1 && originalMinute != -1) {
            ReminderScheduler.scheduleDailyReminder(
                    context,
                    originalHour,
                    originalMinute,
                    content
            );
        }
    }
}