package com.example.myapplication.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.activity.account.AccountActivity;
import com.example.myapplication.activity.addtransaction.AddTransactionActivity;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityHomeBinding;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());

        // 1. Nhận thông tin User truyền từ LoginActivity sang trước tiên
        currentUser = (User) getIntent().getSerializableExtra("EXTRA_USER");

        if (currentUser != null) {
            activityHomeBinding.tvUsername.setText(currentUser.getFullName());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 2. Fragment mặc định khi mở app (kèm thông tin User)
        bottomNavigationView.setSelectedItemId(R.id.navHome);
        setCurrentFragment(createFragmentWithUser(new HomeFragment()));

        // 3. Chuyển Tab BottomNavigation kèm thông tin User
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

        // 4. Mở màn hình Account
        activityHomeBinding.btnAccountAvatar.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, AccountActivity.class);
            i.putExtra("EXTRA_USER", currentUser);
            startActivityForResult(i, REQUEST_ACCOUNT);
        });

        // 5. Nút FAB Thêm Giao Dịch
        activityHomeBinding.fabAddTransaction.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, AddTransactionActivity.class);
            i.putExtra("EXTRA_USER", currentUser);
            startActivity(i);
        });
    }

    /**
     * Hàm đính kèm User vào Bundle của Fragment
     */
    private Fragment createFragmentWithUser(Fragment fragment) {
        if (currentUser != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("EXTRA_USER", currentUser);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    /**
     * Thay thế Fragment hiện tại
     */
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
