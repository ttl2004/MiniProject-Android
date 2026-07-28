package com.example.myapplication.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.data.entity.UserSettings;

@Dao
public interface UserSettingsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserSettings settings);

    @Update
    int update(UserSettings settings);

    @Query("SELECT * FROM user_settings WHERE userId = :userId LIMIT 1")
    LiveData<UserSettings> getSettingsByUserId(int userId);

    @Query("SELECT * FROM user_settings WHERE userId = :userId LIMIT 1")
    UserSettings getSettingsByUserIdSync(int userId);
}