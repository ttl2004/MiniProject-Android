package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.activity.forgotpassword.ForgotPasswordActivity;
import com.example.myapplication.activity.home.HomeActivity;
import com.example.myapplication.activity.register.RegisterActivity;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.manager.UserManager;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userManager = new UserManager(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false);

        if (isLoggedIn) {
            String savedUsername = sharedPreferences.getString("LOGGED_USERNAME", "");

            // Chạy truy vấn DB ngầm để tránh crash UI Thread
            Executors.newSingleThreadExecutor().execute(() -> {
                User currentUser = userManager.getUserByUsername(savedUsername);

                if (currentUser != null) {
                    // Chuyển sang HomeActivity trên UI Thread
                    runOnUiThread(() -> {
                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        i.putExtra("EXTRA_USER", currentUser);
                        startActivity(i);
                        finish();
                    });
                } else {
                    // Nếu không tìm thấy User thì mở lại màn hình đăng nhập
                    runOnUiThread(this::setupLoginUI);
                }
            });
            return;
        }

        setupLoginUI();
    }

    private void setupLoginUI() {
        EdgeToEdge.enable(this);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        // Chuyển sang màn hình Đăng ký
        activityMainBinding.tvRegister.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        // Chuyển sang màn hình Quên mật khẩu
        activityMainBinding.tvForgotPassword.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(i);
        });

        // Nút Đăng nhập
        activityMainBinding.btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String username = activityMainBinding.etUsername.getText() != null
                ? activityMainBinding.etUsername.getText().toString().trim() : "";
        String password = activityMainBinding.etPassword.getText() != null
                ? activityMainBinding.etPassword.getText().toString().trim() : "";

        // Bắt lỗi trống đầu vào
        if (username.isEmpty()) {
            activityMainBinding.etUsername.setError("Vui lòng nhập Tên đăng nhập!");
            activityMainBinding.etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            activityMainBinding.etPassword.setError("Vui lòng nhập Mật khẩu!");
            activityMainBinding.etPassword.requestFocus();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            User res = userManager.login(username, password);

            runOnUiThread(() -> {
                if (res != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("IS_LOGGED_IN", true);
                    editor.putString("LOGGED_USERNAME", res.getUserName());
                    editor.putInt("USER_ID", res.getUserId());
                    editor.apply();

                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    i.putExtra("EXTRA_USER", res);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}