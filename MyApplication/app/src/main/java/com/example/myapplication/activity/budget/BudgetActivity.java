package com.example.myapplication.activity.budget;

import android.content.Intent;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.BudgetAdapter;
import com.example.myapplication.data.dto.BudgetItem;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityBudgetBinding;
import com.example.myapplication.manager.BudgetManager;
import com.example.myapplication.ui.budget.BudgetViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;

public class BudgetActivity extends AppCompatActivity {

    private ActivityBudgetBinding activityBudgetBinding;
    private BudgetViewModel budgetViewModel;
    private BudgetManager budgetManager;
    private BudgetAdapter budgetAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBudgetBinding = ActivityBudgetBinding.inflate(getLayoutInflater());
        setContentView(activityBudgetBinding.getRoot());

        user = (User) getIntent().getSerializableExtra("EXTRA_USER");
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        budgetManager = new BudgetManager(this);

        setupRecyclerView();
        setupEvents();
        observeBudgets();
    }

    private void setupRecyclerView() {
        budgetAdapter = new BudgetAdapter(new ArrayList<>(), new BudgetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BudgetItem budget) {
            }

            @Override
            public void onEditClick(BudgetItem budget) {
                Intent intent = new Intent(BudgetActivity.this, EditBudgetActivity.class);
                intent.putExtra("EXTRA_BUDGET_ID", budget.budgetId);
                intent.putExtra("EXTRA_CATEGORY_ID", budget.categoryId);
                intent.putExtra("EXTRA_LIMIT_AMOUNT", budget.limitAmount);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(BudgetItem budget) {
                confirmDeleteBudget(budget);
            }
        });
        activityBudgetBinding.rcvBudget.setLayoutManager(new LinearLayoutManager(this));
        activityBudgetBinding.rcvBudget.setAdapter(budgetAdapter);
    }

    private void setupEvents() {
        activityBudgetBinding.btnBack.setOnClickListener(v -> finish());
        activityBudgetBinding.btnAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBudgetActivity.class);
            intent.putExtra("EXTRA_USER", user);
            startActivity(intent);
        });
    }

    private void observeBudgets() {
        if (user == null) return;

        budgetViewModel.getBudgetUI(user.getUserId()).observe(this, list -> {
            budgetAdapter.setData(list);

            long totalLeft = 0;
            if (list != null) {
                for (BudgetItem item : list) {
                    totalLeft += item.limitAmount - item.spentAmount;
                }
            }

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            activityBudgetBinding.tvBudgetLeft.setText(formatter.format(totalLeft));
        });
    }

    private void confirmDeleteBudget(BudgetItem budget) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa ngân sách")
                .setMessage("Bạn có chắc muốn xóa ngân sách " + budget.categoryName + "?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        int result = budgetManager.deleteById(budget.budgetId);
                        runOnUiThread(() -> {
                            if (!canUpdateUi()) {
                                return;
                            }

                            if (result <= 0) {
                                new AlertDialog.Builder(this)
                                        .setMessage("Xóa ngân sách thất bại!")
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        });
                    });
                })
                .show();
    }
    private boolean canUpdateUi() {
        return !isFinishing() && !isDestroyed();
    }
}
