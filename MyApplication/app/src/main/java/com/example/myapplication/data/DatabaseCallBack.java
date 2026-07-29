package com.example.myapplication.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

public class DatabaseCallBack extends RoomDatabase.Callback {

    // Functional Interface tự định nghĩa để lấy database một cách an toàn (Lazy)
    public interface DatabaseProvider {
        AppDatabase getDatabase();
    }

    private final DatabaseProvider databaseProvider;

    public DatabaseCallBack(DatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);
        Log.d("Database", "----------Database created, seeding data------------");

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Chỉ lấy instance sau khi Room đã hoàn tất build xong
                AppDatabase database = databaseProvider.getDatabase();
                if (database != null) {
                    DatabaseSeeder.seed(database);
                }
            } catch (Exception e) {
                Log.e("Database", "Lỗi khi seed dữ liệu: " + e.getMessage(), e);
            }
        });
    }
}