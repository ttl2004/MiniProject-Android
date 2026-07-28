package com.example.myapplication.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.data.entity.User;

@Dao
public interface UserDAO {
    @Insert
    long insert(User user);

    @Query("SELECT * FROM users WHERE userName = :userName AND password = :password LIMIT 1")
    User login(String userName, String password);

    @Update
    int update(User user);

    @Query("SELECT * FROM users WHERE userName = :userName LIMIT 1")
    User getUserByUsername(String userName);
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE userName = :userName LIMIT 1)")
    boolean isUsernameExists(String userName);

    @Query("SELECT * FROM users WHERE username = :username AND fullName = :fullName LIMIT 1")
    User findByUsernameAndFullName(String username, String fullName);
}
