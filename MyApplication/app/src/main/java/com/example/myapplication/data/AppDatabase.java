package com.example.myapplication.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.data.dao.UserDAO;
import com.example.myapplication.data.entity.Budget;
import com.example.myapplication.data.entity.Category;
import com.example.myapplication.data.entity.Transaction;
import com.example.myapplication.data.entity.User;

@Database(
        entities = {
                User.class,
                Category.class,
                Budget.class,
                Transaction.class
        },
        version = 2
)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "expense_manager.db";
    public abstract UserDAO userDAO();

    private static volatile AppDatabase instanse;

    public static AppDatabase getInstance(Context context) {
        if (instanse == null) {
            synchronized (AppDatabase.class) {
                if (instanse == null) {
                    instanse = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    ).allowMainThreadQueries().fallbackToDestructiveMigration().build();
                }
            }
        }
        return instanse;
    }
}
