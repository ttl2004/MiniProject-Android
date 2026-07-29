package com.example.myapplication.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyUtils {

    private static final ThreadLocal<DecimalFormat> CURRENCY_FORMATTER = ThreadLocal.withInitial(() -> {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.'); // Dùng dấu chấm phân cách hàng nghìn
        return new DecimalFormat("#,###", symbols);
    });

    public static String formatCurrency(long amount) {
        // Tái sử dụng instance đã được khởi tạo sẵn
        return CURRENCY_FORMATTER.get().format(amount) + " đ";
    }
}