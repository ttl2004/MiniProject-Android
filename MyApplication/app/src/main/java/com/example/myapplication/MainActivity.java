package com.example.myapplication;

import android.content.Intent;
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
        EdgeToEdge.enable(this);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        userManager = new UserManager(this);

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
            Toast.makeText(this, "Dang nhap thanh cong!", Toast.LENGTH_LONG).show();

            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            i.putExtra("EXTRA_USER", res);
            startActivity(i);
        }
        else {
            Toast.makeText(this, "Dang nhap that bai!!!!!!!", Toast.LENGTH_LONG).show();
        }
    }
}