package com.example.myapplication.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberTextWatcher implements TextWatcher {

    private final EditText editText;
    private String current = "";

    public NumberTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().equals(current)) {
            editText.removeTextChangedListener(this);

            // Xóa tất cả dấu chấm cũ để lấy chuỗi số thuần túy
            String cleanString = s.toString().replaceAll("[.]", "");

            if (!cleanString.isEmpty()) {
                try {
                    double parsed = Double.parseDouble(cleanString);

                    // Cấu hình định dạng dùng dấu . làm phân cách hàng nghìn
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
                    symbols.setGroupingSeparator('.');

                    DecimalFormat formatter = new DecimalFormat("#,###", symbols);
                    String formatted = formatter.format(parsed);

                    current = formatted;
                    editText.setText(formatted);
                    editText.setSelection(formatted.length()); // Đưa con trỏ về cuối
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                current = "";
                editText.setText("");
            }

            editText.addTextChangedListener(this);
        }
    }
}