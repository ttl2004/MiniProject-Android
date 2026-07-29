package com.example.myapplication.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityUpdateAccountBinding;
import com.example.myapplication.manager.UserManager;

import java.util.concurrent.Executors;

public class UpdateAccountActivity extends AppCompatActivity {
    private static final int MIN_FULL_NAME_LENGTH = 4;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private ActivityUpdateAccountBinding binding;
    private UserManager userManager;
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new UserManager(this);
        currentUser = (User) getIntent().getSerializableExtra("EXTRA_USER");

        showCurrentUserInfo();
        initEvents();
    }

    private void showCurrentUserInfo() {
        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.etFullName.setText(currentUser.getFullName());
        binding.etUserName.setText(currentUser.getUserName());
        binding.tilUserName.setEnabled(false);
        binding.etUserName.setEnabled(false);
        binding.etUserName.setFocusable(false);
    }

    private void initEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSaveInfo.setOnClickListener(v -> saveUserInfo());
    }

    private void saveUserInfo() {
        clearInputErrors();

        String fullName = binding.etFullName.getText().toString().trim();
        String oldPassword = binding.etOldPassword.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // 1. Validate dữ liệu cơ bản (chạy trên Main Thread)
        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        if (fullName.length() < MIN_FULL_NAME_LENGTH) {
            binding.tilFullName.setError("Tên đầy đủ phải có ít nhất 4 ký tự!");
            return;
        }

        boolean wantsToChangePassword = !TextUtils.isEmpty(oldPassword)
                || !TextUtils.isEmpty(password)
                || !TextUtils.isEmpty(confirmPassword);

        if (wantsToChangePassword) {
            if (TextUtils.isEmpty(oldPassword)) {
                binding.tilOldPassword.setError("Vui lòng nhập mật khẩu hiện tại!");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                binding.tilPassword.setError("Vui lòng nhập mật khẩu mới");
                return;
            }

            if (password.length() < MIN_PASSWORD_LENGTH) {
                binding.tilPassword.setError("Mật khẩu phải có ít nhất 8 ký tự!");
                return;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                binding.tilConfirmPassword.setError("Vui lòng nhập lại mật khẩu!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                binding.tilConfirmPassword.setError("Mật khẩu nhập lại không khớp!");
                return;
            }

            if (!oldPassword.equals(currentUser.getPassword())) {
                binding.tilOldPassword.setError("Mật khẩu hiện tại không đúng!");
                return;
            }

            if (password.equals(oldPassword)) {
                binding.tilPassword.setError("Mật khẩu mới không được trùng với mật khẩu hiện tại!");
                return;
            }

            currentUser.setPassword(password);
        }

        currentUser.setFullName(fullName);
        currentUser.setUpdatedAt(System.currentTimeMillis());

        // Vô hiệu hóa nút Save để tránh spam click
        binding.btnSaveInfo.setEnabled(false);

        // 2. Đẩy thao tác UPDATE sang Luồng Ngầm (Background Thread)
        Executors.newSingleThreadExecutor().execute(() -> {
            int updatedRows = userManager.update(currentUser);

            // Cập nhật lại UI sau khi thực thi DB xong
            runOnUiThread(() -> {
                binding.btnSaveInfo.setEnabled(true);

                if (updatedRows > 0) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("EXTRA_USER", currentUser);
                    setResult(RESULT_OK, resultIntent);
                    Toast.makeText(UpdateAccountActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateAccountActivity.this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void clearInputErrors() {
        binding.tilFullName.setError(null);
        binding.tilUserName.setError(null);
        binding.tilOldPassword.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);
    }
}