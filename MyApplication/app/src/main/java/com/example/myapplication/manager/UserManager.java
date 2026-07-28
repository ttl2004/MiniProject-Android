package com.example.myapplication.manager;

import android.content.Context;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.data.repository.UserRepository;

public class UserManager {
    private UserRepository userRepository;

    public UserManager(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userRepository = new UserRepository(db.userDAO());
    }

    public long register(User user) {
        return userRepository.register(user);
    }

    public User login(String username, String password) {
        return userRepository.login(username, password);
    }

    public int update(User user) {
        return userRepository.update(user);
    }

    public User getUserByUsername(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return null;
        }
        return userRepository.getUserByUsername(userName);
    }

    public boolean isUsernameExists(String userName) {
        if (userName == null || userName.trim().isEmpty()) return false;
        return userRepository.isUsernameExists(userName);
    }

    public User getUserByUsernameAndFullName(String username, String fullName) {
        return userRepository.findByUsernameAndFullName(username, fullName);
    }
}
