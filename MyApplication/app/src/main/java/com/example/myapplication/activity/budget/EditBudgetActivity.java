package com.example.myapplication.activity.budget;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.data.entity.Category;
import com.example.myapplication.databinding.FragmentAddBudgetBinding;
import com.example.myapplication.manager.BudgetManager;
import com.example.myapplication.manager.CategoryManager;
import com.example.myapplication.utils.NumberTextWatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EditBudgetActivity extends AppCompatActivity {
    private FragmentAddBudgetBinding binding;
    private CategoryAdapter categoryAdapter;
    private CategoryManager categoryManager;
    private BudgetManager budgetManager;
    private final List<Category> categoryList = new ArrayList<>();
    private Category selectedCategory;
    private int budgetId;
    private int selectedCategoryId;
    private long limitAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAddBudgetBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        budgetId = getIntent().getIntExtra("EXTRA_BUDGET_ID", -1);
        selectedCategoryId = getIntent().getIntExtra("EXTRA_CATEGORY_ID", -1);
        limitAmount = getIntent().getLongExtra("EXTRA_LIMIT_AMOUNT", 0);

        categoryManager = new CategoryManager(this);
        budgetManager = new BudgetManager(this);

        binding.tvTitle.setText("Sửa ngân sách");
        binding.budgetPeriodCard.setVisibility(View.GONE);
        setupRecyclerView();
        setupEvents();
        observeCategories(); // Đã sửa để chỉ lấy danh mục Chi tiêu

        binding.tvAmount.addTextChangedListener(new NumberTextWatcher(binding.tvAmount));
        binding.tvAmount.setText(String.valueOf(limitAmount));
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
        binding.btnSave.setText("Cập nhật ngân sách");
        binding.btnSave.setOnClickListener(v -> updateBudget());
    }

    /**
     * Chỉ nạp các danh mục thuộc loại CHI TIÊU ("EXPENSE")
     */
    private void observeCategories() {
        categoryManager.getCategoriesByType("EXPENSE").observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                Toast.makeText(this, "Chưa có danh mục chi tiêu nào!", Toast.LENGTH_SHORT).show();
                categoryList.clear();
                categoryAdapter.setCategoryList(new ArrayList<>());
                selectedCategory = null;
                return;
            }

            categoryList.clear();
            categoryList.addAll(categories);
            categoryAdapter.setCategoryList(categoryList);

            // Highlight lại đúng danh mục cũ của Ngân sách đang sửa
            categoryAdapter.selectCategoryById(selectedCategoryId);
            selectedCategory = categoryAdapter.getSelectedCategory();
        });
    }

    private void updateBudget() {
        if (budgetId <= 0) {
            Toast.makeText(this, "Không tìm thấy ngân sách cần sửa!", Toast.LENGTH_SHORT).show();
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

        Executors.newSingleThreadExecutor().execute(() -> {
            int result = budgetManager.updateBudget(
                    budgetId,
                    selectedCategory.getId(),
                    (int) amountValue
            );

            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(this, "Cập nhật ngân sách thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật ngân sách thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}