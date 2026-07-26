package com.example.myapplication.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityAccountBinding;

public class AccountActivity extends AppCompatActivity {
    private static final int REQUEST_UPDATE_ACCOUNT = 1001;
    private ActivityAccountBinding activityAccountBinding;
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAccountBinding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(activityAccountBinding.getRoot());

        currentUser = (User) getIntent().getSerializableExtra("EXTRA_USER");
        showUserInfo();

        activityAccountBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithUpdatedUser();
            }
        });

        activityAccountBinding.btnChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, UpdateAccountActivity.class);
                intent.putExtra("EXTRA_USER", currentUser);
                startActivityForResult(intent, REQUEST_UPDATE_ACCOUNT);
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

    private void showUserInfo() {
        if (currentUser != null) {
            activityAccountBinding.tvAccountName.setText(currentUser.getFullName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_ACCOUNT && resultCode == RESULT_OK && data != null) {
            currentUser = (User) data.getSerializableExtra("EXTRA_USER");
            showUserInfo();
            setUpdatedUserResult();
        }
    }

    private void setUpdatedUserResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("EXTRA_USER", currentUser);
        setResult(RESULT_OK, resultIntent);
    }

    private void finishWithUpdatedUser() {
        setUpdatedUserResult();
        finish();
    }

    @Override
    public void onBackPressed() {
        finishWithUpdatedUser();
    }
}
