package com.example.myapplication.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.dao.TransactionDAO;
import com.example.myapplication.data.entity.Transaction;

import java.util.List;

public class TransactionRepository {
    private final TransactionDAO transactionDAO;

    public TransactionRepository(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public long insert(Transaction transaction) {
        return transactionDAO.insert(transaction);
    }

    public LiveData<List<Transaction>> getTransactionsByUserIdAndRange(int userId, long startDate, long endDate) {
        return transactionDAO.getTransactionsByUserIdAndRange(userId, startDate, endDate);
    }

    public void delete(Transaction transaction) {
        transactionDAO.deleteTransaction(transaction);
    }

    public void update(Transaction transaction) {
        transactionDAO.updateTransaction(transaction);
    }

}
