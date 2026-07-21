package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Manager.UserManager;
import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.data.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity {
    private EditText fullName;
    private EditText userName;
    private EditText password;
    private Button exit;
    private Button register;

    private UserManager userManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        userManager = new UserManager(this);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register.setOnClickListener(v -> register());

    }

    private void init() {
        fullName = findViewById(R.id.editTextTextMultiLine_Register_FullName);
        userName = findViewById(R.id.editTextTextMultiLine_Register_UserName);
        password = findViewById(R.id.editTextTextMultiLine_Register_Password);
        exit = findViewById(R.id.button_Register_Exit);
        register = findViewById(R.id.button_Register);
    }

    private void register() {
        String fullname = fullName.getText().toString().trim();
        String Username = userName.getText().toString().trim();
        String Password = password.getText().toString().trim();
        User user = new User(fullname,Username,Password);

        long res = userManager.register(user);

        if (res > 0) {
            Toast.makeText(this, "Dang ky thanh cong!", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            Toast.makeText(this, "Dang ky that bai!!!!!!!", Toast.LENGTH_LONG).show();
        }
    }
}
