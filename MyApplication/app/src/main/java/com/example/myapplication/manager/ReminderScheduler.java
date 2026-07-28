package com.example.myapplication.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.myapplication.receiver.AlarmReceiver;

import java.util.Calendar;

public class ReminderScheduler {

    public static void scheduleDailyReminder(Context context, int hour, int minute, String note) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // Hủy báo thức cũ trước khi đặt mới
        cancelDailyReminder(context);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("EXTRA_NOTE", note);
        // TRUYỀN GIỜ/PHÚT GỐC VÀO INTENT ĐỂ TỰ HẸN LẠI CHO NGÀY MAI
        intent.putExtra("EXTRA_HOUR", hour);
        intent.putExtra("EXTRA_MINUTE", minute);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                100,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Nếu giờ chọn <= thời gian hiện tại -> Hẹn sang ngày mai
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // ĐẶT BÁO THỨC CHÍNH XÁC (Tránh bị trễ do Doze Mode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    public static void cancelDailyReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                100,
                intent,
                PendingIntent.FLAG_NO_CREATE | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}