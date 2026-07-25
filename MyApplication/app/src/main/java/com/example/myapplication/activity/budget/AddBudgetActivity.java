package com.example.myapplication.activity.budget;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.data.entity.Budget;
import com.example.myapplication.data.entity.Category;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.FragmentAddBudgetBinding;
import com.example.myapplication.manager.BudgetManager;
import com.example.myapplication.manager.CategoryManager;
import com.example.myapplication.utils.NumberTextWatcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class AddBudgetActivity extends AppCompatActivity {
    private FragmentAddBudgetBinding binding;
    private CategoryAdapter categoryAdapter;
    private CategoryManager categoryManager;
    private BudgetManager budgetManager;
    private final List<Category> categoryList = new ArrayList<>();
    private Category selectedCategory;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAddBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) getIntent().getSerializableExtra("EXTRA_USER");
        categoryManager = new CategoryManager(this);
        budgetManager = new BudgetManager(this);

        binding.tvTitle.setText("Thiết lập ngân sách mới");
        setupRecyclerView();
        setupEvents();
        observeCategories();
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(categoryList, category -> selectedCategory = category);
        binding.rcvCategory.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rcvCategory.setAdapter(categoryAdapter);
    }

    private void setupEvents() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveBudget());
        binding.tvAmount.addTextChangedListener(new NumberTextWatcher(binding.tvAmount));
    }

    private void observeCategories() {
        categoryManager.getALL().observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                Toast.makeText(this, "Chưa có danh mục nào!", Toast.LENGTH_SHORT).show();
                return;
            }

            categoryList.clear();
            categoryList.addAll(categories);
            categoryAdapter.setCategoryList(categoryList);
            selectedCategory = categoryAdapter.getSelectedCategory();
        });
    }

    private void saveBudget() {
        if (user == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Vui lòng chọn danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountInput = binding.tvAmount.getText().toString().replace(".", "").trim();
        long amountValue = amountInput.isEmpty() ? 0 : Long.parseLong(amountInput);
        if (amountValue <= 0) {
            Toast.makeText(this, "Vui lòng nhập hạn mức!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountValue > Integer.MAX_VALUE) {
            Toast.makeText(this, "Hạn mức quá lớn!", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = (int) amountValue;
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        Budget budget = new Budget(user.getUserId(), selectedCategory.getId(), amount, month);

        Executors.newSingleThreadExecutor().execute(() -> {
            long result = budgetManager.insert(budget);
            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(this, "Lưu ngân sách thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lưu ngân sách thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
