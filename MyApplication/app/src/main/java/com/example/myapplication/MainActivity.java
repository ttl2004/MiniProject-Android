package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.Manager.UserManager;
import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.data.repository.UserRepository;

public class MainActivity extends AppCompatActivity {

    private EditText userName;
    private EditText password;
    private Button login;
    private Button register;
    private UserManager userManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();

        userManager = new UserManager(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);

                startActivity(i);

            }
        });

        login.setOnClickListener(v -> login());
    }

    private void init() {
        userName = findViewById(R.id.editTextTextMultiLine_Main_UserName);
        password = findViewById(R.id.editTextNumberPassword_Main);
        login = findViewById(R.id.button_Main_Login);
        register = findViewById(R.id.button_Main_Register);
    }

    private void login() {
        String Username = userName.getText().toString().trim();
        String Password = password.getText().toString().trim();

        User res = userManager.login(Username, Password);

        if (res != null) {
            Toast.makeText(this, "Dang nhap thanh cong!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Dang nhap that bai!!!!!!!", Toast.LENGTH_LONG).show();
        }
    }
}