package com.example.myapplication.activity.addtransaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.data.entity.Category;
import com.example.myapplication.data.entity.Transaction;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.ActivityAddTransactionBinding;
import com.example.myapplication.manager.TransactionManager;
import com.example.myapplication.utils.NumberTextWatcher;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddTransactionActivity extends AppCompatActivity {

    private ActivityAddTransactionBinding binding;
    private CategoryAdapter categoryAdapter;
    private TransactionManager transactionManager;
    private List<Category> categoryList = new ArrayList<>();

    private String selectedType = "EXPENSE"; // Mặc định là Chi tiêu
    private Calendar selectedCalendar = Calendar.getInstance();
    private Category selectedCategory = null;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding giao diện
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Manager
        transactionManager = new TransactionManager(this);

        // Lấy thông tin user hiện tại
        user= (User) getIntent().getSerializableExtra("EXTRA_USER");

        setupRecyclerView();
        setupEvents();
        updateDateDisplay();
        observeCategories();
    }

    private void setupRecyclerView() {
        // Dùng đúng CategoryAdapter của bạn
        categoryAdapter = new CategoryAdapter(
                categoryList,
                category -> selectedCategory = category
        );

        binding.rvCategories.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void setupEvents() {
        // Nút Close
        binding.btnClose.setOnClickListener(v -> finish());

        // Chuyển tab Chi tiêu / Thu nhập
        binding.tabExpense.setOnClickListener(v -> switchType("EXPENSE"));
        binding.tabIncome.setOnClickListener(v -> switchType("INCOME"));

        // Chọn ngày
        View.OnClickListener dateClickListener = v -> showDatePicker();
        binding.btnPickDate.setOnClickListener(dateClickListener);
        binding.tvDate.setOnClickListener(dateClickListener);

        // Nút Lưu Giao Dịch
        binding.btnSaveTransaction.setOnClickListener(v -> saveTransaction());

        // Đăng ký tự động thêm dấu chấm khi gõ
        binding.edtAmount.addTextChangedListener(new NumberTextWatcher(binding.edtAmount));
    }

    private void observeCategories() {
        // Lấy danh sách Category qua LiveData của TransactionManager
        transactionManager.getALLCategories().observe(this, dbCategories -> {
            if (dbCategories != null && !dbCategories.isEmpty()) {
                categoryList = dbCategories;
                categoryAdapter.setCategoryList(categoryList);

                // Mặc định chọn item đầu tiên nếu chưa chọn
                if (selectedCategory == null) {
                    selectedCategory = categoryAdapter.getSelectedCategory();
                }
            } else {
                Toast.makeText(this, "Chưa có danh mục nào trong CSDL!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchType(String type) {
        selectedType = type;
        if ("EXPENSE".equals(type)) {
            binding.tabExpense.setBackgroundResource(R.drawable.bg_toggle_active);
            binding.tabExpense.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            binding.tabIncome.setBackgroundResource(android.R.color.transparent);
            binding.tabIncome.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        } else {
            binding.tabIncome.setBackgroundResource(R.drawable.bg_toggle_active);
            binding.tabIncome.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            binding.tabExpense.setBackgroundResource(android.R.color.transparent);
            binding.tabExpense.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("'Hôm nay, 'dd' tháng 'MM", new Locale("vi", "VN"));
        binding.tvDate.setText(sdf.format(selectedCalendar.getTime()));
    }

    private void saveTransaction() {
        String amountStr = binding.edtAmount.getText().toString().replaceAll("[.]", "").trim();
        String note = binding.edtNote.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Vui lòng chọn một danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        long transactionDate = selectedCalendar.getTimeInMillis();

        // Tạo Entity Transaction
        Transaction transaction = new Transaction(
                user.getUserId(),
                selectedCategory.getId(),
                amount,
                note,
                transactionDate,
                selectedType
        );

        // Lưu CSDL thông qua TransactionManager trên Background Thread
        Executors.newSingleThreadExecutor().execute(() -> {
            long result = transactionManager.insert(transaction);

            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(this, "Lưu giao dịch thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lưu giao dịch thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}