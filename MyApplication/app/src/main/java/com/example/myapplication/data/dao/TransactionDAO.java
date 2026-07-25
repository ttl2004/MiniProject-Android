package com.example.myapplication.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.myapplication.data.entity.Transaction;

@Dao
public interface TransactionDAO {
    @Insert
    long insert(Transaction transaction);

}
