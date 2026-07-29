package com.example.myapplication.activity.forgotpassword;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityForgotPasswordBinding;
import com.example.myapplication.manager.UserManager;

import java.util.concurrent.Executors;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new UserManager(this);

        setupListeners();
    }

    private void setupListeners() {
        binding.btnGetPassword.setOnClickListener(v -> handleGetPassword());
        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void handleGetPassword() {
        String fullName = binding.etForgotFullname.getText() != null
                ? binding.etForgotFullname.getText().toString().trim() : "";
        String username = binding.etForgotUsername.getText() != null
                ? binding.etForgotUsername.getText().toString().trim() : "";

        // Validate dữ liệu đầu vào (UI Thread)
        if (fullName.isEmpty()) {
            binding.etForgotFullname.setError("Vui lòng nhập Họ và tên!");
            binding.etForgotFullname.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            binding.etForgotUsername.setError("Vui lòng nhập Tên đăng nhập!");
            binding.etForgotUsername.requestFocus();
            return;
        }

        // Vô hiệu hóa nút để tránh người dùng bấm liên tục
        binding.btnGetPassword.setEnabled(false);

        // 🟢 Đẩy truy vấn DB sang Luồng Ngầm
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userManager.getUserByUsernameAndFullName(username, fullName);

            // Cập nhật lại UI sau khi truy vấn xong
            runOnUiThread(() -> {
                binding.btnGetPassword.setEnabled(true);

                if (user != null) {
                    binding.layoutResult.setVisibility(View.VISIBLE);
                    binding.tvPasswordResult.setText(user.getPassword());
                    Toast.makeText(ForgotPasswordActivity.this, "Tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
                } else {
                    // Không tìm thấy tài khoản tương ứng
                    binding.layoutResult.setVisibility(View.GONE);
                    Toast.makeText(ForgotPasswordActivity.this, "Họ tên hoặc Tên đăng nhập không chính xác!", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}