package com.example.myapplication.data.repository;

import com.example.myapplication.data.dao.TransactionDAO;
import com.example.myapplication.data.entity.Transaction;

public class TransactionRepository {
    private final TransactionDAO transactionDAO;

    public TransactionRepository(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public long insert(Transaction transaction) {
        return transactionDAO.insert(transaction);
    }
}
