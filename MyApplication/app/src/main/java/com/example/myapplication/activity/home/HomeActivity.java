package com.example.myapplication.activity.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activity.notification.NotificationActivity;
import com.example.myapplication.activity.account.AccountActivity;
import com.example.myapplication.activity.addtransaction.AddTransactionActivity;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityHomeBinding;
import com.example.myapplication.manager.NotificationManager;
import com.example.myapplication.ui.analysis.AnalysisFragment;
import com.example.myapplication.ui.budget.BudgetFragment;
import com.example.myapplication.ui.category.CategoryFragment;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.ui.transaction.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private static final int REQUEST_ACCOUNT = 2001;
    private ActivityHomeBinding activityHomeBinding;
    private User currentUser;
    private NotificationManager notificationManager; // Declared NotificationManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());

        // Khởi tạo NotificationManager
        notificationManager = new NotificationManager(this);

        // 1. Xử lý nút Back (Hiện dialog xác nhận)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmationDialog();
            }
        });

        // 2. Nhận thông tin User truyền từ LoginActivity sang
        currentUser = (User) getIntent().getSerializableExtra("EXTRA_USER");

        if (currentUser != null) {
            activityHomeBinding.tvUsername.setText(currentUser.getFullName());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 3. Fragment mặc định khi mở app
        bottomNavigationView.setSelectedItemId(R.id.navHome);
        setCurrentFragment(createFragmentWithUser(new HomeFragment()));

        // 4. Chuyển Tab BottomNavigation
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Fragment targetFragment;

            if (id == R.id.navHome) {
                targetFragment = new HomeFragment();
            } else if (id == R.id.navAnalysis) {
                targetFragment = new AnalysisFragment();
            } else if (id == R.id.navBudget) {
                targetFragment = new BudgetFragment();
            } else if (id == R.id.navCategory) {
                targetFragment = new CategoryFragment();
            } else {
                targetFragment = new TransactionFragment();
            }

            setCurrentFragment(createFragmentWithUser(targetFragment));
            return true;
        });

        // 5. Mở màn hình Account
        activityHomeBinding.btnAccountAvatar.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, AccountActivity.class);
            i.putExtra("EXTRA_USER", currentUser);
            startActivityForResult(i, REQUEST_ACCOUNT);
        });

        // 6. Nút FAB Thêm Giao Dịch
        activityHomeBinding.fabAddTransaction.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, AddTransactionActivity.class);
            i.putExtra("EXTRA_USER", currentUser);
            startActivity(i);
        });

        // 7. Xử lý sự kiện mở màn hình Thông Báo
        activityHomeBinding.btnNotification.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(i);
        });

        // 8. Lắng nghe số lượng thông báo chưa đọc để cập nhật badge
        setupNotificationBadge();
    }


    private void setupNotificationBadge() {
        notificationManager.getUnreadCount().observe(this, unreadCount -> {
            if (unreadCount != null && unreadCount > 0) {
                activityHomeBinding.tvNotificationBadge.setVisibility(View.VISIBLE);
                activityHomeBinding.tvNotificationBadge.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
            } else {
                activityHomeBinding.tvNotificationBadge.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Dialog xác nhận thoát
     */
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có chắc chắn muốn thoát khỏi ứng dụng không?")
                .setPositiveButton("Thoát", (dialog, which) -> finishAffinity()) // Đóng tất cả activity
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private Fragment createFragmentWithUser(Fragment fragment) {
        if (currentUser != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("EXTRA_USER", currentUser);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ACCOUNT && resultCode == RESULT_OK && data != null) {
            currentUser = (User) data.getSerializableExtra("EXTRA_USER");
            if (currentUser != null) {
                getIntent().putExtra("EXTRA_USER", currentUser);
                activityHomeBinding.tvUsername.setText(currentUser.getFullName());
            }
        }
    }
}