package com.example.myapplication.ui.month;
public class MonthModel {
    private final String displayName; // Ví dụ: "Tháng 7"
    private final int month;          // 0-indexed (0 = Tháng 1, 11 = Tháng 12)
    private final int year;           // Năm tương ứng

    public MonthModel(String displayName, int month, int year) {
        this.displayName = displayName;
        this.month = month;
        this.year = year;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
