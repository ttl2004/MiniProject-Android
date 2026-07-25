package com.example.myapplication.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyUtils {

    public static String formatCurrency(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.'); // Dùng dấu chấm phân cách

        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(amount) + "đ";
    }
}