package com.example.myapplication.data.dto;

public class AnalysisCategoryDTO {
    private int categoryId;
    private String categoryName;
    private String iconName;
    private String categoryColor;
    private long totalAmount;
    private float percentage;

    public AnalysisCategoryDTO(int categoryId, String categoryName, String iconName, String categoryColor, long totalAmount, float percentage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.iconName = iconName;
        this.categoryColor = categoryColor;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getIconName() {
        return iconName;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
}
