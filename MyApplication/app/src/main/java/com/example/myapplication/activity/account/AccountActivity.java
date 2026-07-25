package com.example.myapplication.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityAccountBinding;
import com.google.android.material.button.MaterialButton;

public class AccountActivity extends AppCompatActivity {
    private ActivityAccountBinding activityAccountBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAccountBinding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(activityAccountBinding.getRoot());


        activityAccountBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        activityAccountBinding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AccountActivity.this, "Dang xuat thanh cong!", Toast.LENGTH_LONG).show();

                Intent i = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
