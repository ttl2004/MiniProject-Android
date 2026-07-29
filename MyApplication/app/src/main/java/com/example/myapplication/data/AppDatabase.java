package com.example.myapplication.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myapplication.data.dao.BudgetDAO;
import com.example.myapplication.data.dao.CategoryDAO;
import com.example.myapplication.data.dao.NotificationDAO;
import com.example.myapplication.data.dao.TransactionDAO;
import com.example.myapplication.data.dao.UserDAO;
import com.example.myapplication.data.dao.UserSettingsDAO;
import com.example.myapplication.data.entity.Budget;
import com.example.myapplication.data.entity.Category;
import com.example.myapplication.data.entity.Notification;
import com.example.myapplication.data.entity.Transaction;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.data.entity.UserSettings;

@Database(
        entities = {
                User.class,
                Category.class,
                Budget.class,
                Transaction.class,
                UserSettings.class,
                Notification.class
        },
        version = 6
)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "expense_manager.db";
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "DELETE FROM budgets " +
                            "WHERE id NOT IN (" +
                            "SELECT MAX(id) FROM budgets GROUP BY userId, categoryId, month, year" +
                            ")"
            );
            database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_budgets_userId_categoryId_month_year " +
                            "ON budgets(userId, categoryId, month, year)"
            );
        }
    };

    // DAO
    public abstract UserDAO userDAO();
    public abstract CategoryDAO categoryDAO();
    public abstract BudgetDAO budgetDAO();
    public abstract TransactionDAO transactionDAO();
    public abstract UserSettingsDAO userSettingsDAO();
    public abstract NotificationDAO notificationDAO();
    private static volatile AppDatabase instanse;

    public static AppDatabase getInstance(Context context) {
        if (instanse == null) {
            synchronized (AppDatabase.class) {
                if (instanse == null) {
                    instanse = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    ).allowMainThreadQueries()
                            .addCallback(new DatabaseCallBack(context))
                            .addMigrations(MIGRATION_4_5)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instanse;
    }
}
