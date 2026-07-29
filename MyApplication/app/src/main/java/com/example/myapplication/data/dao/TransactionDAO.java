package com.example.myapplication.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.data.entity.Transaction;

import java.util.List;

@Dao
public interface TransactionDAO {
    @Insert
    long insert(Transaction transaction);

    // Query lấy giao dịch trong khoảng thời gian (Từ đầu tháng đến cuối tháng)
    @Query("SELECT * FROM transactions WHERE userId = :userId AND transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate DESC")
    LiveData<List<Transaction>> getTransactionsByUserIdAndRange(int userId, long startDate, long endDate);


    // Xóa giao dịch trực tiếp bằng Entity
    @Delete
    void deleteTransaction(Transaction transaction);

    // Cập nhật giao dịch trực tiếp bằng Entity
    @Update
    void updateTransaction(Transaction transaction);

    @Query("SELECT t.*, " +
            "COALESCE(c.name, 'Khác') AS categoryName, " +
            "COALESCE(c.icon, '') AS iconName, " +
            "COALESCE(c.color, '#E0E0E0') AS categoryColor " +
            "FROM transactions t " +
            "LEFT JOIN categories c ON t.categoryId = c.id " +
            "WHERE t.userId = :userId AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY t.transactionDate DESC")
    LiveData<List<TransactionDTO>> getTransactionsWithCategoryByRange(int userId, long startDate, long endDate);
}
