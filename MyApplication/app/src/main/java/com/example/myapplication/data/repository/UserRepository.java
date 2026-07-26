package com.example.myapplication.data.repository;

import com.example.myapplication.data.dao.UserDAO;
import com.example.myapplication.data.entity.User;

public class UserRepository {
    private final UserDAO userDAO;

    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public long register(User user) {
        return userDAO.insert(user);
    }

    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

    public int update(User user) {
        return userDAO.update(user);
    }

    public User getUserByUsername(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return null;
        }
        return userDAO.getUserByUsername(userName);
    }

    public boolean isUsernameExists(String userName) {
        return userDAO.isUsernameExists(userName);
    }
}
