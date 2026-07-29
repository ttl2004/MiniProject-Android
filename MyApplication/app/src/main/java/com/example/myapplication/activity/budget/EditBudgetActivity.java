package com.example.myapplication.activity.budget;

import android.database.sqlite.SQLiteConstraintException;
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
import com.example.myapplication.utils.ViewUtils;

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

    // Cờ trạng thái chống spam click
    private boolean isSaving = false;

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
        observeCategories();

        binding.tvAmount.addTextChangedListener(new NumberTextWatcher(binding.tvAmount));
        binding.tvAmount.setText(String.valueOf(limitAmount));

        ViewUtils.disablePaste(binding.tvAmount);
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

            categoryAdapter.selectCategoryById(selectedCategoryId);
            selectedCategory = categoryAdapter.getSelectedCategory();
        });
    }

    /**
     * Bật / Tắt trạng thái nút Cập nhật để tránh bấm nhiều lần
     */
    private void setSaveButtonEnabled(boolean enabled) {
        binding.btnSave.setEnabled(enabled);
        binding.btnSave.setAlpha(enabled ? 1.0f : 0.5f);
    }

    private void updateBudget() {
        // Chặn nếu đang xử lý cập nhật
        if (isSaving) return;

        if (budgetId <= 0) {
            Toast.makeText(this, "Không tìm thấy ngân sách cần sửa!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Vui lòng chọn danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        Long amountValue = parseLimitAmount();
        if (amountValue == null) {
            Toast.makeText(this, "Hạn mức quá lớn!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountValue <= 0) {
            Toast.makeText(this, "Vui lòng nhập hạn mức!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Khóa nút bấm lập tức khi bắt đầu chạy luồng lưu
        isSaving = true;
        setSaveButtonEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            int result;
            try {
                result = budgetManager.updateBudget(
                        budgetId,
                        selectedCategory.getId(),
                        amountValue
                );
            } catch (SQLiteConstraintException e) {
                runOnUiThread(() -> {
                    isSaving = false;
                    setSaveButtonEnabled(true);
                    if (canUpdateUi()) {
                        Toast.makeText(this, "Ngân sách cho danh mục này đã tồn tại trong tháng đã chọn!", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            } catch (Exception e) {
                runOnUiThread(() -> {
                    isSaving = false;
                    setSaveButtonEnabled(true);
                    if (canUpdateUi()) {
                        Toast.makeText(this, "Cập nhật ngân sách thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            runOnUiThread(() -> {
                if (!canUpdateUi()) return;

                if (result > 0) {
                    Toast.makeText(this, "Cập nhật ngân sách thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish(); // Quay lại màn hình ngân sách
                } else {
                    isSaving = false;
                    setSaveButtonEnabled(true);
                    Toast.makeText(this, "Cập nhật ngân sách thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private Long parseLimitAmount() {
        String amountInput = binding.tvAmount.getText().toString().replaceAll("\\D", "").trim();
        if (amountInput.isEmpty()) {
            return 0L;
        }

        try {
            return Long.parseLong(amountInput);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean canUpdateUi() {
        return !isFinishing() && !isDestroyed();
    }
}