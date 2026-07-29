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

    private Transaction currentTransaction = null;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding giao diện
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Manager
        transactionManager = new TransactionManager(this);

        // Lấy thông tin user hiện tại
        user = (User) getIntent().getSerializableExtra("EXTRA_USER");

        // KIỂM TRA XEM CÓ TRUYỀN TRANSACTION CŨ SANG KHÔNG (Chế độ Edit)
        if (getIntent().hasExtra("EXTRA_TRANSACTION")) {
            currentTransaction = (Transaction) getIntent().getSerializableExtra("EXTRA_TRANSACTION");
            if (currentTransaction != null) {
                isEditMode = true;
            }
        }

        setupRecyclerView();
        setupEvents();

        // Cấu hình giao diện ban đầu
        if (isEditMode) {
            setupEditModeUI();
        } else {
            updateDateDisplay();
            // Mặc định chọn tab EXPENSE và load danh mục tương ứng
            switchType("EXPENSE");
        }
    }

    private void setupRecyclerView() {
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

        // Nút Lưu / Cập nhật Giao Dịch
        binding.btnSaveTransaction.setOnClickListener(v -> saveTransaction());

        // Tự động định dạng dấu chấm phân cách số tiền khi gõ
        binding.edtAmount.addTextChangedListener(new NumberTextWatcher(binding.edtAmount));
    }

    private void setupEditModeUI() {
        if (currentTransaction == null) return;

        // 1. Đổi tiêu đề và chữ trên nút bấm
        binding.tvTitle.setText("Chỉnh sửa giao dịch");
        binding.btnSaveTransaction.setText("CẬP NHẬT GIAO DỊCH");

        // 2. Load số tiền cũ
        long absAmount = Math.abs(currentTransaction.getAmount());
        binding.edtAmount.setText(String.valueOf(absAmount));

        // 3. Load ghi chú
        if (currentTransaction.getNote() != null) {
            binding.edtNote.setText(currentTransaction.getNote());
        }

        // 4. Load ngày thực hiện
        selectedCalendar.setTimeInMillis(currentTransaction.getTransactionDate());
        updateDateDisplay();

        // 5. Chuyển đúng tab (EXPENSE/INCOME) -> Tự động load danh mục tương ứng
        switchType(currentTransaction.getType() != null ? currentTransaction.getType() : "EXPENSE");
    }

    /**
     * Chuyển đổi tab Chi tiêu / Thu nhập & Gọi load lại danh mục
     */
    private void switchType(String type) {
        selectedType = type;
        selectedCategory = null; // Reset danh mục đã chọn trước đó

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

        // Tải danh mục theo loại (EXPENSE / INCOME)
        loadCategoriesByType(type);
    }

    /**
     * Lọc và nạp danh sách danh mục từ CSDL theo type
     */
    private void loadCategoriesByType(String type) {
        transactionManager.getCategoriesByType(type).observe(this, dbCategories -> {
            if (dbCategories != null && !dbCategories.isEmpty()) {
                categoryList = dbCategories;
                categoryAdapter.setCategoryList(categoryList);

                // Nếu ở chế độ EDIT & cùng type với giao dịch cũ: Highlight đúng Category cũ
                if (isEditMode && currentTransaction != null && type.equals(currentTransaction.getType())) {
                    for (Category cat : categoryList) {
                        if (cat.getId() == currentTransaction.getCategoryId()) {
                            selectedCategory = cat;
                            categoryAdapter.setSelectedCategory(cat);
                            break;
                        }
                    }
                }

                // Nếu chưa chọn được category nào (hoặc vừa đổi tab): Tự động chọn item đầu tiên
                if (selectedCategory == null && !categoryList.isEmpty()) {
                    selectedCategory = categoryList.get(0);
                    categoryAdapter.setSelectedCategory(selectedCategory);
                }
            } else {
                categoryList.clear();
                categoryAdapter.setCategoryList(new ArrayList<>());
                selectedCategory = null;
                Toast.makeText(this, "Chưa có danh mục nào cho loại này!", Toast.LENGTH_SHORT).show();
            }
        });
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
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        SimpleDateFormat sdfBase = new SimpleDateFormat("dd 'tháng' MM", new Locale("vi", "VN"));

        if (isSameDay(selectedCalendar, today)) {
            binding.tvDate.setText("Hôm nay, " + sdfBase.format(selectedCalendar.getTime()));
        } else if (isSameDay(selectedCalendar, yesterday)) {
            binding.tvDate.setText("Hôm qua, " + sdfBase.format(selectedCalendar.getTime()));
        } else {
            SimpleDateFormat sdfFull = new SimpleDateFormat("dd 'tháng' MM, yyyy", new Locale("vi", "VN"));
            binding.tvDate.setText(sdfFull.format(selectedCalendar.getTime()));
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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

        long amount;
        try {
            amount = Long.parseLong(amountStr);

            // Bắt lỗi nếu người dùng nhập 0 đồng
            if (amount <= 0) {
                //Toast.makeText(this, "Số tiền phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                binding.edtAmount.setError("Số tiền phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            // Bắt lỗi tràn số khi người dùng nhập quá 19 chữ số
           // Toast.makeText(this, "Số tiền quá lớn, vượt quá giới hạn cho phép!", Toast.LENGTH_SHORT).show();
            binding.edtAmount.setError("Số tiền quá lớn, vượt quá giới hạn cho phép!");
            binding.edtAmount.requestFocus();
            return;
        }
        long transactionDate = selectedCalendar.getTimeInMillis();

        if (isEditMode && currentTransaction != null) {
            // 1. CẬP NHẬT GIAO DỊCH CŨ
            currentTransaction.setCategoryId(selectedCategory.getId());
            currentTransaction.setAmount(amount);
            currentTransaction.setNote(note);
            currentTransaction.setTransactionDate(transactionDate);
            currentTransaction.setType(selectedType);

            Executors.newSingleThreadExecutor().execute(() -> {
                transactionManager.update(currentTransaction);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Cập nhật giao dịch thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });

        } else {
            // 2. THÊM GIAO DỊCH MỚI
            Transaction transaction = new Transaction(
                    user != null ? user.getUserId() : 0,
                    selectedCategory.getId(),
                    amount,
                    note,
                    transactionDate,
                    selectedType
            );

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
}