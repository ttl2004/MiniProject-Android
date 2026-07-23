package com.example.myapplication.activity.register;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Manager.UserManager;
import com.example.myapplication.R;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding activityRegisterBinding;
    private UserManager userManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(activityRegisterBinding.getRoot());



        userManager = new UserManager(this);

        activityRegisterBinding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        activityRegisterBinding.btnRegister.setOnClickListener(v -> register());

    }

    private void register() {
        String fullname = activityRegisterBinding.etRegisterFullname.getText().toString().trim();
        String Username = activityRegisterBinding.etRegisterUsername.getText().toString().trim();
        String Password = activityRegisterBinding.etRegisterPassword.getText().toString().trim();
        String confirmPassword = activityRegisterBinding.etConfirmPassword.getText().toString().trim();

        if (!Password.equals(confirmPassword)) {
            Toast.makeText(this, "Mat khau khong giong nhau!", Toast.LENGTH_LONG).show();
        }
        else {
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
}
