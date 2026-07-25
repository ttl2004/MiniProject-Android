package com.example.myapplication.ui.transaction.model;

import com.example.myapplication.data.dto.TransactionDTO;

import java.util.List;

public class TransactionGroup {
    private String dateHeader;
    private long totalAmount;
    private List<TransactionDTO> transactions; // Dùng DTO tại đây

    public TransactionGroup() {
    }

    public TransactionGroup(String dateHeader, long totalAmount, List<TransactionDTO> transactions) {
        this.dateHeader = dateHeader;
        this.totalAmount = totalAmount;
        this.transactions = transactions;
    }

    public String getDateHeader() { return dateHeader; }
    public void setDateHeader(String dateHeader) { this.dateHeader = dateHeader; }

    public long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(long totalAmount) { this.totalAmount = totalAmount; }

    public List<TransactionDTO> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionDTO> transactions) { this.transactions = transactions; }
}