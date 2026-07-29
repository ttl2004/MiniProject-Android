package com.example.myapplication.activity.budget;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
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
    private static final int YEAR_PICKER_RANGE = 10;

    private FragmentAddBudgetBinding binding;
    private CategoryAdapter categoryAdapter;
    private CategoryManager categoryManager;
    private BudgetManager budgetManager;
    private final List<Category> categoryList = new ArrayList<>();
    private Category selectedCategory;
    private User user;
    private int selectedMonth;
    private int selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAddBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) getIntent().getSerializableExtra("EXTRA_USER");
        categoryManager = new CategoryManager(this);
        budgetManager = new BudgetManager(this);

        binding.tvTitle.setText("Thiết lập ngân sách mới");
        setupDefaultPeriod();
        setupRecyclerView();
        setupEvents();
        observeCategories(); // Nạp duy nhất các danh mục Chi tiêu
    }

    private void setupDefaultPeriod() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        selectedMonth = getIntent().getIntExtra("EXTRA_BUDGET_MONTH", currentMonth);
        selectedYear = getIntent().getIntExtra("EXTRA_BUDGET_YEAR", currentYear);

        if (isPastPeriod(selectedMonth, selectedYear)) {
            selectedMonth = currentMonth;
            selectedYear = currentYear;
        }

        updatePeriodText();
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
        binding.tvBudgetMonthPicker.setOnClickListener(v -> showMonthPicker());
        binding.tvBudgetYearPicker.setOnClickListener(v -> showYearPicker());
        binding.tvAmount.addTextChangedListener(new NumberTextWatcher(binding.tvAmount));
    }

    private void showMonthPicker() {
        String[] monthItems = buildMonthItems();

        new AlertDialog.Builder(this)
                .setTitle("Chọn tháng")
                .setItems(monthItems, (dialog, which) -> {
                    selectedMonth = getFirstSelectableMonth() + which;
                    updatePeriodText();
                })
                .show();
    }

    private String[] buildMonthItems() {
        int firstMonth = getFirstSelectableMonth();
        int count = 12 - firstMonth + 1;
        String[] items = new String[count];

        for (int i = 0; i < count; i++) {
            items[i] = "Tháng " + (firstMonth + i);
        }

        return items;
    }

    private int getFirstSelectableMonth() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        return selectedYear == currentYear ? currentMonth : 1;
    }

    private void showYearPicker() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        String[] yearItems = new String[YEAR_PICKER_RANGE + 1];

        for (int i = 0; i < yearItems.length; i++) {
            yearItems[i] = String.valueOf(currentYear + i);
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn năm")
                .setItems(yearItems, (dialog, which) -> {
                    selectedYear = currentYear + which;
                    if (isPastPeriod(selectedMonth, selectedYear)) {
                        selectedMonth = getFirstSelectableMonth();
                    }
                    updatePeriodText();
                })
                .show();
    }

    private void updatePeriodText() {
        binding.tvBudgetMonthPicker.setText("Tháng " + selectedMonth);
        binding.tvBudgetYearPicker.setText(String.valueOf(selectedYear));
    }

    private boolean isPastPeriod(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        return year < currentYear || (year == currentYear && month < currentMonth);
    }

    /**
     * Chỉ nạp các danh mục CHI TIÊU ("EXPENSE")
     */
    private void observeCategories() {
        categoryManager.getCategoriesByType("EXPENSE").observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                Toast.makeText(this, "Chưa có danh mục Chi tiêu nào!", Toast.LENGTH_SHORT).show();
                categoryList.clear();
                categoryAdapter.setCategoryList(new ArrayList<>());
                selectedCategory = null;
                return;
            }

            categoryList.clear();
            categoryList.addAll(categories);
            categoryAdapter.setCategoryList(categoryList);

            if (!categoryList.isEmpty()) {
                selectedCategory = categoryList.get(0);
                categoryAdapter.setSelectedCategory(selectedCategory);
            }
        });
    }

    private void saveBudget() {
        if (user == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Vui lòng chọn danh mục chi tiêu!", Toast.LENGTH_SHORT).show();
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

        if (isPastPeriod(selectedMonth, selectedYear)) {
            Toast.makeText(this, "Không thể đặt hạn mức cho tháng đã qua!", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount = amountValue;
        Budget budget = new Budget(
                user.getUserId(),
                selectedCategory.getId(),
                amount,
                selectedMonth,
                selectedYear
        );

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
            Budget existingBudget = budgetManager.getBudgetByCategoryPeriod(
                    user.getUserId(),
                    selectedCategory.getId(),
                    selectedMonth,
                    selectedYear
            );

            if (existingBudget != null) {
                runOnUiThread(() -> {
                    if (canUpdateUi()) {
                        showExistingBudgetDialog(existingBudget);
                    }
                });
                return;
            }

            long result;
            try {
                result = budgetManager.insert(budget);
            } catch (SQLiteConstraintException e) {
                Budget conflictedBudget = budgetManager.getBudgetByCategoryPeriod(
                        user.getUserId(),
                        selectedCategory.getId(),
                        selectedMonth,
                        selectedYear
                );
                runOnUiThread(() -> {
                    if (!canUpdateUi()) {
                        return;
                    }

                    if (conflictedBudget != null) {
                        showExistingBudgetDialog(conflictedBudget);
                    } else {
                        Toast.makeText(this, "Ngân sách cho danh mục này đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (!canUpdateUi()) {
                        return;
                    }

                    Toast.makeText(this, "Lưu ngân sách thất bại!", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            runOnUiThread(() -> {
                if (!canUpdateUi()) {
                    return;
                }

                if (result > 0) {
                    Toast.makeText(this, "Lưu ngân sách thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lưu ngân sách thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (canUpdateUi()) {
                        Toast.makeText(this, "Lưu ngân sách thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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

    private void showExistingBudgetDialog(Budget existingBudget) {
        if (!canUpdateUi()) {
            return;
        }

        String categoryName = selectedCategory == null ? "danh mục này" : selectedCategory.getName();

        new AlertDialog.Builder(this)
                .setTitle("Ngân sách đã tồn tại")
                .setMessage("Ngân sách cho " + categoryName + " tháng " + selectedMonth + "/" + selectedYear + " đã tồn tại. Bạn có muốn sửa hạn mức này không?")
                .setNegativeButton("Không", null)
                .setPositiveButton("Sửa", (dialog, which) -> openEditBudget(existingBudget))
                .show();
    }

    private boolean canUpdateUi() {
        return !isFinishing() && !isDestroyed();
    }

    private void openEditBudget(Budget budget) {
        Intent intent = new Intent(this, EditBudgetActivity.class);
        intent.putExtra("EXTRA_BUDGET_ID", budget.getId());
        intent.putExtra("EXTRA_CATEGORY_ID", budget.getCategoryId());
        intent.putExtra("EXTRA_LIMIT_AMOUNT", (long) budget.getLimitAmount());
        startActivity(intent);
        finish();
    }
}
