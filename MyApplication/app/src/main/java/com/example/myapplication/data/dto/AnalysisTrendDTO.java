package com.example.myapplication.data.dto;

public class AnalysisTrendDTO {
    private String dateLabel;
    private long totalAmount; // Tổng tiền (chi hoặc thu)

    public AnalysisTrendDTO(String dateLabel, long totalAmount) {
        this.dateLabel = dateLabel;
        this.totalAmount = totalAmount;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
