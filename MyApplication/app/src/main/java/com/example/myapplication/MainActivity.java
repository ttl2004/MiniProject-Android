package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.manager.UserManager;
import com.example.myapplication.activity.home.HomeActivity;
import com.example.myapplication.activity.register.RegisterActivity;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userManager = new UserManager(this);

        // --- BƯỚC 1: KIỂM TRA TRẠNG THÁI ĐĂNG NHẬP TRƯỚC ĐÓ ---
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false);

        if (isLoggedIn) {
            String savedUsername = sharedPreferences.getString("LOGGED_USERNAME", "");
            User currentUser = userManager.getUserByUsername(savedUsername); // Đảm bảo UserManager có hàm lấy User theo Username

            if (currentUser != null) {
                // Đã đăng nhập trước đó -> Vào thẳng HomeActivity luôn
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                i.putExtra("EXTRA_USER", currentUser);
                startActivity(i);
                finish(); // Đóng LoginActivity
                return;
            }
        }

        // --- BƯỚC 2: NẾU CHƯA ĐĂNG NHẬP THÌ MỚI VẼ GIAO DIỆN LOGIN ---
        EdgeToEdge.enable(this);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        activityMainBinding.btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String Username = activityMainBinding.etUsername.getText().toString().trim();
        String Password = activityMainBinding.etPassword.getText().toString().trim();

        User res = userManager.login(Username, Password);

        if (res != null) {
            // --- BƯỚC 3: LƯU THÔNG TIN ĐĂNG NHẬP VÀO SHAREDPREFERENCES ---
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("IS_LOGGED_IN", true);
            editor.putString("LOGGED_USERNAME", res.getUserName());
            editor.apply();

            Toast.makeText(this, "Dang nhap thanh cong!", Toast.LENGTH_LONG).show();

            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            i.putExtra("EXTRA_USER", res);
            startActivity(i);

            finish(); // Đóng LoginActivity để người dùng không Back lại màn này được
        } else {
            Toast.makeText(this, "Dang nhap that bai!!!!!!!", Toast.LENGTH_LONG).show();
        }
    }
}