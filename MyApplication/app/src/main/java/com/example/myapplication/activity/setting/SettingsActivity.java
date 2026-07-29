package com.example.myapplication.activity.setting;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.data.entity.User;
import com.example.myapplication.data.entity.UserSettings;
import com.example.myapplication.databinding.ActivitySettingsBinding;
import com.example.myapplication.manager.ReminderScheduler;
import com.example.myapplication.manager.UserSettingsManager;

import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private ActivitySettingsBinding binding;
    private UserSettingsManager settingsManager;
    private int currentUserId = -1;
    private int selectedHour = 20, selectedMinute = 0;
    private boolean isInitialLoading = true; // Cờ tránh trigger save khi load data lần đầu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Lấy User từ Intent
        User currentUser = (User) getIntent().getSerializableExtra("EXTRA_USER");

        if (currentUser != null) {
            currentUserId = currentUser.getUserId();
        } else {
            // 2. Dự phòng: Lấy từ SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            currentUserId = sharedPreferences.getInt("USER_ID", -1);
        }

        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        settingsManager = new UserSettingsManager(this);

        setupListeners();
        loadData();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        // Chọn giờ
        binding.btnSelectTime.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                String timeStr = String.format("%02d:%02d", selectedHour, selectedMinute);
                binding.tvTime.setText(timeStr);
                binding.tvDailyDesc.setText("Nhận báo cáo tổng hợp vào " + timeStr);
                saveSettings();
            }, selectedHour, selectedMinute, true);
            dialog.show();
        });

        // Switch Nhắc nhở
        binding.switchDaily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isInitialLoading) return;

            if (isChecked) {
                checkAndRequestNotificationPermission();
            }
            saveSettings();
        });
    }

    private void loadData() {
        settingsManager.getSettingsByUserId(currentUserId).observe(this, settings -> {
            if (settings != null) {
                selectedHour = settings.getReminderHour();
                selectedMinute = settings.getReminderMinute();
                String timeStr = String.format("%02d:%02d", selectedHour, selectedMinute);

                binding.tvTime.setText(timeStr);
                binding.tvDailyDesc.setText("Nhận báo cáo tổng hợp vào " + timeStr);

                if (settings.getReminderNote() != null && !settings.getReminderNote().isEmpty()) {
                    binding.edtReminderContent.setText(settings.getReminderNote());
                }

                // Cập nhật trạng thái Switch Nhắc nhở
                binding.switchDaily.setChecked(settings.isReminderEnabled());
            }
            // Tắt cờ load sau khi binding dữ liệu xong
            isInitialLoading = false;
        });
    }

    private void saveSettings() {
        if (isInitialLoading) return;

        UserSettings settings = new UserSettings(currentUserId);
        settings.setReminderEnabled(binding.switchDaily.isChecked());
        settings.setReminderHour(selectedHour);
        settings.setReminderMinute(selectedMinute);

        String note = binding.edtReminderContent.getText().toString().trim();
        settings.setReminderNote(note.isEmpty() ? "Đừng quên ghi chép chi tiêu hôm nay nhé!" : note);

        // Đặt hoặc Hủy báo thức
        if (settings.isReminderEnabled()) {
            ReminderScheduler.scheduleDailyReminder(this, currentUserId,selectedHour, selectedMinute, settings.getReminderNote());
        } else {
            ReminderScheduler.cancelDailyReminder(this);
        }

        // Lưu vào Room DB trên background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            UserSettings existing = settingsManager.getSettingsByUserIdSync(currentUserId);
            if (existing == null) {
                settingsManager.insert(settings);
            } else {
                settingsManager.update(settings);
            }
        });
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bạn cần cấp quyền thông báo để nhận nhắc nhở!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSettings();
    }
}