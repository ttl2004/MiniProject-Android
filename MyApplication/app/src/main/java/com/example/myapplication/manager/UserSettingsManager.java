package com.example.myapplication.manager;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.entity.UserSettings;
import com.example.myapplication.data.repository.UserSettingsRepository;

public class UserSettingsManager {
    private UserSettingsRepository userSettingsRepository;

    public UserSettingsManager(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userSettingsRepository = new UserSettingsRepository(db.userSettingsDAO());
    }

    public long insert(UserSettings settings) {
        return userSettingsRepository.insert(settings);
    }

    public int update(UserSettings settings) {
        return userSettingsRepository.update(settings);
    }

    public LiveData<UserSettings> getSettingsByUserId(int userId) {
        return userSettingsRepository.getSettingsByUserId(userId);
    }

    public UserSettings getSettingsByUserIdSync(int userId) {
        return userSettingsRepository.getSettingsByUserIdSync(userId);
    }
}