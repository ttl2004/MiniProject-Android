package com.example.myapplication.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.activity.addtransaction.AddTransactionActivity;
import com.example.myapplication.adapter.MonthAdapter;
import com.example.myapplication.adapter.TransactionDetailAdapter;
import com.example.myapplication.adapter.TransactionGroupAdapter;
import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.FragmentTransactionBinding;
import com.example.myapplication.ui.month.MonthModel;
import com.example.myapplication.ui.transaction.model.TransactionGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionFragment extends Fragment {

    private FragmentTransactionBinding binding;
    private TransactionViewModel viewModel;

    private MonthAdapter monthAdapter;
    private TransactionGroupAdapter groupAdapter;

    private User currentUser;
    private String currentTab = "ALL";
    private List<TransactionDTO> currentRawList = new ArrayList<>();

    private int selectedYear;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("EXTRA_USER");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        if (currentUser != null) {
            viewModel.setUserId(currentUser.getUserId());
        }

        selectedYear = Calendar.getInstance().get(Calendar.YEAR);

        setupYearNavigation();
        setupMonthRecyclerView();
        setupGroupRecyclerView();
        setupTabClickListeners();
        observeData();

        loadMonthsForYear(selectedYear);
    }

    private void setupYearNavigation() {
        binding.tvSelectedYear.setText(String.valueOf(selectedYear));

        binding.btnPrevYear.setOnClickListener(v -> changeYear(-1));

        binding.btnNextYear.setOnClickListener(v -> {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (selectedYear < currentYear) {
                changeYear(1);
            }
        });
    }

    private void changeYear(int offset) {
        selectedYear += offset;
        binding.tvSelectedYear.setText(String.valueOf(selectedYear));
        loadMonthsForYear(selectedYear);
    }

    private List<MonthModel> generateMonthListForYear(int targetYear) {
        List<MonthModel> list = new ArrayList<>();

        Calendar currentCal = Calendar.getInstance();
        int currentYear = currentCal.get(Calendar.YEAR);
        int currentMonth = currentCal.get(Calendar.MONTH);

        int maxMonth = (targetYear == currentYear) ? currentMonth : 11;

        for (int m = maxMonth; m >= 0; m--) {
            list.add(new MonthModel("Tháng " + (m + 1), m, targetYear));
        }

        return list;
    }

    private void loadMonthsForYear(int year) {
        List<MonthModel> monthList = generateMonthListForYear(year);

        if (monthAdapter == null) {
            monthAdapter = new MonthAdapter(monthList, (monthModel, position) -> {
                viewModel.selectMonth(monthModel);
            });
            binding.rvMonths.setAdapter(monthAdapter);
        } else {
            monthAdapter.setMonthList(monthList);
        }

        if (!monthList.isEmpty()) {
            MonthModel defaultMonth = monthList.get(0);
            viewModel.selectMonth(defaultMonth);
        }
    }

    private void setupMonthRecyclerView() {
        binding.rvMonths.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
    }

    private void setupGroupRecyclerView() {
        groupAdapter = new TransactionGroupAdapter(new ArrayList<>(), new TransactionDetailAdapter.OnTransactionActionListener() {
            @Override
            public void onEdit(TransactionDTO dto) {
                // Chuyển sang AddTransactionActivity ở chế độ Sửa
                openEditTransactionActivity(dto);
            }

            @Override
            public void onDelete(TransactionDTO dto) {
                // Hiển thị Dialog xác nhận xóa
                showDeleteDialog(dto);
            }
        });

        binding.rvGroupTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvGroupTransactions.setAdapter(groupAdapter);
    }

    private void showDeleteDialog(TransactionDTO dto) {
        String categoryName = (dto.getCategoryName() != null && !dto.getCategoryName().isEmpty())
                ? dto.getCategoryName() : "giao dịch này";

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa giao dịch")
                .setMessage("Bạn có chắc chắn muốn xóa " + categoryName + "?")
                .setNegativeButton("HỦY", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("XÓA", (dialog, which) -> {
                    if (dto.getTransaction() != null) {
                        viewModel.deleteTransaction(dto.getTransaction());
                    }
                })
                .show();
    }

    // --- Mở màn hình AddTransactionActivity ở chế độ Chỉnh sửa ---
    private void openEditTransactionActivity(TransactionDTO dto) {
        if (dto == null || dto.getTransaction() == null) return;

        Intent intent = new Intent(requireContext(), AddTransactionActivity.class);
        intent.putExtra("EXTRA_USER", currentUser);
        intent.putExtra("EXTRA_TRANSACTION", dto.getTransaction());
        startActivity(intent);
    }

    private void setupTabClickListeners() {
        binding.tabAll.setOnClickListener(v -> switchTab("ALL"));
        binding.tabExpense.setOnClickListener(v -> switchTab("EXPENSE"));
        binding.tabIncome.setOnClickListener(v -> switchTab("INCOME"));
    }

    private void switchTab(String tabType) {
        currentTab = tabType;

        binding.tabAll.setBackgroundResource(android.R.color.transparent);
        binding.tabAll.setTextColor(0xFF757575);
        binding.tabExpense.setBackgroundResource(android.R.color.transparent);
        binding.tabExpense.setTextColor(0xFF757575);
        binding.tabIncome.setBackgroundResource(android.R.color.transparent);
        binding.tabIncome.setTextColor(0xFF757575);

        if ("EXPENSE".equalsIgnoreCase(tabType)) {
            binding.tabExpense.setBackgroundResource(com.example.myapplication.R.drawable.bg_toggle_active);
            binding.tabExpense.setTextColor(0xFFFFFFFF);
        } else if ("INCOME".equalsIgnoreCase(tabType)) {
            binding.tabIncome.setBackgroundResource(com.example.myapplication.R.drawable.bg_toggle_active);
            binding.tabIncome.setTextColor(0xFFFFFFFF);
        } else {
            binding.tabAll.setBackgroundResource(com.example.myapplication.R.drawable.bg_toggle_active);
            binding.tabAll.setTextColor(0xFFFFFFFF);
        }

        renderData();
    }

    private void observeData() {
        viewModel.rawTransactions.observe(getViewLifecycleOwner(), transactions -> {
            currentRawList = transactions != null ? transactions : new ArrayList<>();
            renderData();
        });
    }

    private void renderData() {
        List<TransactionGroup> groupedList = viewModel.processAndGroupTransactions(currentRawList, currentTab);
        groupAdapter.setGroupList(groupedList);

        if (groupedList.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.rvGroupTransactions.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.rvGroupTransactions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}