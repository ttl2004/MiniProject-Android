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
    private final DecimalFormat formatter;

    public NumberTextWatcher(EditText editText) {
        this.editText = editText;

        // Cấu hình cố định định dạng dấu chấm (.) làm phân cách hàng nghìn
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        this.formatter = new DecimalFormat("#,###", symbols);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().equals(current)) {
            editText.removeTextChangedListener(this);

            // Xóa tất cả ký tự không phải số (thay vì chỉ xóa dấu chấm [.] cũ)
            String cleanString = s.toString().replaceAll("[^\\d]", "");

            if (!cleanString.isEmpty()) {
                try {
                    // Dùng Long.parseLong() thay cho Double.parseDouble()
                    long parsed = Long.parseLong(cleanString);

                    String formatted = formatter.format(parsed);

                    current = formatted;
                    editText.setText(formatted);

                    // Đưa con trỏ chuột về cuối chuỗi
                    editText.setSelection(formatted.length());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                current = "";
                editText.setText("");
            }

            editText.addTextChangedListener(this);

            // BẮT BUỘC: Ép EditText đo đạc lại kích thước ngay lập tức
            // Dòng này hỗ trợ AutoSize co chữ lại khi chuỗi dài ra
            editText.requestLayout();
        }
    }
}