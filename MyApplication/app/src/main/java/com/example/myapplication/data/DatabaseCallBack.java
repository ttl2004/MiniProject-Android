package com.example.myapplication.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

public class DatabaseCallBack extends RoomDatabase.Callback {
    private final Context context;

    public DatabaseCallBack(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);
        Log.d("Database", "----------done------------");
        Executors.newSingleThreadExecutor().execute(() ->{
            AppDatabase database = AppDatabase.getInstance(context);
            DatabaseSeeder.seed(database);
        });
    }
}