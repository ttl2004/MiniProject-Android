package com.example.myapplication.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.MonthAdapter;
import com.example.myapplication.adapter.TransactionGroupAdapter;
import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.FragmentTransactionBinding;
import com.example.myapplication.ui.month.MonthModel;
import com.example.myapplication.ui.transaction.model.TransactionGroup;

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

    private int selectedYear; // Năm đang chọn (VD: 2026)

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

        // 1. Set userId cho ViewModel
        if (currentUser != null) {
            viewModel.setUserId(currentUser.getUserId());
        }

        // Mặc định chọn năm hiện tại
        selectedYear = Calendar.getInstance().get(Calendar.YEAR);

        setupYearNavigation();
        setupMonthRecyclerView();
        setupGroupRecyclerView();
        setupTabClickListeners();
        observeData();

        // 2. Load danh sách tháng cho năm hiện tại & gọi DB tháng mới nhất
        loadMonthsForYear(selectedYear);
    }

    // --- Điều hướng Chuyển Năm (Chuẩn ID theo XML) ---
    private void setupYearNavigation() {
        binding.tvSelectedYear.setText(String.valueOf(selectedYear));

        // Nút Lùi năm: btn_prev_year
        binding.btnPrevYear.setOnClickListener(v -> changeYear(-1));

        // Nút Tiến năm: btn_next_year (Chặn không cho vượt quá năm hiện tại)
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

        // Nạp lại danh sách tháng cho năm vừa đổi
        loadMonthsForYear(selectedYear);
    }

    // --- Logic Sinh Danh Sách Tháng (Loại bỏ các tháng trong tương lai) ---
    private List<MonthModel> generateMonthListForYear(int targetYear) {
        List<MonthModel> list = new ArrayList<>();

        Calendar currentCal = Calendar.getInstance();
        int currentYear = currentCal.get(Calendar.YEAR);
        int currentMonth = currentCal.get(Calendar.MONTH); // 0-indexed (0 = Tháng 1)

        // Nếu là năm hiện tại: Tháng cao nhất là Tháng Hiện Tại (Không dính tháng tương lai)
        // Nếu là năm quá khứ: Tháng cao nhất là Tháng 12 (Index 11)
        int maxMonth = (targetYear == currentYear) ? currentMonth : 11;

        // Sinh danh sách từ Tháng mới nhất lùi về Tháng 1 (Index 0)
        for (int m = maxMonth; m >= 0; m--) {
            list.add(new MonthModel("Tháng " + (m + 1), m, targetYear));
        }

        return list;
    }

    private void loadMonthsForYear(int year) {
        List<MonthModel> monthList = generateMonthListForYear(year);

        if (monthAdapter == null) {
            monthAdapter = new MonthAdapter(monthList, (monthModel, position) -> {
                // Người dùng click chọn tháng -> ViewModel trigger query lại DB
                viewModel.selectMonth(monthModel);
            });
            binding.rvMonths.setAdapter(monthAdapter);
        } else {
            // Cập nhật danh sách mới vào Adapter
            monthAdapter.setMonthList(monthList);
        }

        // Tự động chọn Tháng mới nhất (đầu danh sách) -> Trigger gọi DB ngay lập tức
        if (!monthList.isEmpty()) {
            MonthModel defaultMonth = monthList.get(0);
            viewModel.selectMonth(defaultMonth);
        }
    }

    // --- Cấu hình RecyclerView & Tab ---
    private void setupMonthRecyclerView() {
        binding.rvMonths.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
    }

    private void setupGroupRecyclerView() {
        groupAdapter = new TransactionGroupAdapter(new ArrayList<>());
        binding.rvGroupTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvGroupTransactions.setAdapter(groupAdapter);
    }

    private void setupTabClickListeners() {
        binding.tabAll.setOnClickListener(v -> switchTab("ALL"));
        binding.tabExpense.setOnClickListener(v -> switchTab("EXPENSE"));
        binding.tabIncome.setOnClickListener(v -> switchTab("INCOME"));
    }

    private void switchTab(String tabType) {
        currentTab = tabType;

        // Reset UI nền cho các Tab Toggle
        binding.tabAll.setBackgroundResource(android.R.color.transparent);
        binding.tabAll.setTextColor(0xFF757575);
        binding.tabExpense.setBackgroundResource(android.R.color.transparent);
        binding.tabExpense.setTextColor(0xFF757575);
        binding.tabIncome.setBackgroundResource(android.R.color.transparent);
        binding.tabIncome.setTextColor(0xFF757575);

        // Active tab được chọn
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