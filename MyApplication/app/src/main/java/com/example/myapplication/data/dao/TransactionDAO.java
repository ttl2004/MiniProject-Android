package com.example.myapplication.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.data.entity.Transaction;

import java.util.List;

@Dao
public interface TransactionDAO {
    @Insert
    long insert(Transaction transaction);

    // Query lấy giao dịch trong khoảng thời gian (Từ đầu tháng đến cuối tháng)
    @Query("SELECT * FROM transactions WHERE userId = :userId AND transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate DESC")
    LiveData<List<Transaction>> getTransactionsByUserIdAndRange(int userId, long startDate, long endDate);

}
