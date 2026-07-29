package com.example.myapplication.activity.register;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityRegisterBinding;
import com.example.myapplication.manager.UserManager;

import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding activityRegisterBinding;
    private UserManager userManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(activityRegisterBinding.getRoot());

        userManager = new UserManager(this);

        activityRegisterBinding.tvLogin.setOnClickListener(v -> finish());

        activityRegisterBinding.btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String fullname = activityRegisterBinding.etRegisterFullname.getText().toString().trim();
        String username = activityRegisterBinding.etRegisterUsername.getText().toString().trim();
        String password = activityRegisterBinding.etRegisterPassword.getText().toString().trim();
        String confirmPassword = activityRegisterBinding.etConfirmPassword.getText().toString().trim();

        // 1. Reset các lỗi hiển thị trước đó (chạy trên UI Thread)
        clearErrors();

        // 2. Validate dữ liệu cơ bản (chưa đụng DB)
        if (fullname.length() < 4) {
            showErrorFullname("Tên đầy đủ phải có ít nhất 4 ký tự!");
            return;
        }

        if (username.length() < 8) {
            showErrorUsername("Tên đăng nhập phải có ít nhất 8 ký tự!");
            return;
        }

        if (password.length() < 8) {
            showErrorPassword("Mật khẩu phải có ít nhất 8 ký tự!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorConfirmPassword("Mật khẩu xác nhận không trùng khớp!");
            return;
        }

        // Vô hiệu hóa nút Đăng ký để tránh spam click trong lúc chờ DB xử lý
        activityRegisterBinding.btnRegister.setEnabled(false);

        // 3. Đẩy kiểm tra DB và Insert sang Luồng Ngầm (Background Thread)
        Executors.newSingleThreadExecutor().execute(() -> {

            // a. Kiểm tra trùng Username dưới DB
            boolean isExists = userManager.isUsernameExists(username);

            if (isExists) {
                // Trả lỗi về UI Thread nếu username đã tồn tại
                runOnUiThread(() -> {
                    activityRegisterBinding.btnRegister.setEnabled(true);
                    showErrorUsername("Tên đăng nhập này đã tồn tại!");
                });
                return;
            }

            // b. Thực hiện Insert User mới vào DB
            User user = new User(fullname, username, password);
            long res = userManager.register(user);

            // c. Cập nhật UI kết quả
            runOnUiThread(() -> {
                activityRegisterBinding.btnRegister.setEnabled(true);

                if (res > 0) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Các hàm phụ trợ hiển thị lỗi lên giao diện
    private void clearErrors() {
        activityRegisterBinding.etRegisterFullname.setError(null);
        activityRegisterBinding.etRegisterUsername.setError(null);
        activityRegisterBinding.etRegisterPassword.setError(null);
        activityRegisterBinding.etConfirmPassword.setError(null);
    }

    private void showErrorFullname(String message) {
        activityRegisterBinding.etRegisterFullname.setError(message);
        activityRegisterBinding.etRegisterFullname.requestFocus();
    }

    private void showErrorUsername(String message) {
        activityRegisterBinding.etRegisterUsername.setError(message);
        activityRegisterBinding.etRegisterUsername.requestFocus();
    }

    private void showErrorPassword(String message) {
        activityRegisterBinding.etRegisterPassword.setError(message);
        activityRegisterBinding.etRegisterPassword.requestFocus();
    }

    private void showErrorConfirmPassword(String message) {
        activityRegisterBinding.etConfirmPassword.setError(message);
        activityRegisterBinding.etConfirmPassword.requestFocus();
    }
}