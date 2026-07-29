package com.example.myapplication.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.dao.UserSettingsDAO;
import com.example.myapplication.data.entity.UserSettings;

public class UserSettingsRepository {
    private final UserSettingsDAO userSettingsDAO;

    public UserSettingsRepository(UserSettingsDAO userSettingsDAO) {
        this.userSettingsDAO = userSettingsDAO;
    }

    public long insert(UserSettings settings) {
        return userSettingsDAO.insert(settings);
    }

    public int update(UserSettings settings) {
        return userSettingsDAO.update(settings);
    }

    public LiveData<UserSettings> getSettingsByUserId(int userId) {
        return userSettingsDAO.getSettingsByUserId(userId);
    }

    public UserSettings getSettingsByUserIdSync(int userId) {
        return userSettingsDAO.getSettingsByUserIdSync(userId);
    }
}