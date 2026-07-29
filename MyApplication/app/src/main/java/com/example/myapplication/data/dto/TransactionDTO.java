package com.example.myapplication.data.dto;

import androidx.room.Embedded;

import com.example.myapplication.data.entity.Transaction;

public class TransactionDTO {
    @Embedded
    private Transaction transaction;
    private String categoryName;
    private String iconName;
    private String categoryColor; // Thêm mã màu Hex (VD: "#FF5722" hoặc "#3F51B5")

    public TransactionDTO(Transaction transaction, String categoryName, String iconName, String categoryColor) {
        this.transaction = transaction;
        this.categoryName = categoryName;
        this.iconName = iconName;
        this.categoryColor = categoryColor;
    }

    public Transaction getTransaction() { return transaction; }
    public String getCategoryName() { return categoryName; }
    public String getIconName() { return iconName; }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }
}