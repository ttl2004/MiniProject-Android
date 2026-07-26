package com.example.myapplication.ui.budget;

import android.app.AlertDialog;
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

import com.example.myapplication.activity.budget.AddBudgetActivity;
import com.example.myapplication.activity.budget.EditBudgetActivity;
import com.example.myapplication.adapter.BudgetAdapter;
import com.example.myapplication.data.dto.BudgetItem;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.FragmentBudgetBinding;
import com.example.myapplication.manager.BudgetManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class BudgetFragment extends Fragment {
    private FragmentBudgetBinding binding;
    private BudgetViewModel budgetViewModel;
    private BudgetAdapter budgetAdapter;
    private BudgetManager budgetManager;
    private User user;
    private int selectedMonth;
    private int selectedYear;

    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = (User) requireActivity().getIntent().getSerializableExtra("EXTRA_USER");
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        budgetManager = new BudgetManager(requireContext());

        setupDefaultPeriod();
        setupRecyclerView();
        setupEvents();
        observeBudgets();
    }

    private void setupDefaultPeriod() {
        Calendar calendar = Calendar.getInstance();
        selectedMonth = calendar.get(Calendar.MONTH) + 1;
        selectedYear = calendar.get(Calendar.YEAR);
        updatePeriodText();
    }

    private void setupRecyclerView() {
        budgetAdapter = new BudgetAdapter(new ArrayList<>(), new BudgetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BudgetItem budget) {
            }

            @Override
            public void onEditClick(BudgetItem budget) {
                openEditBudget(budget);
            }

            @Override
            public void onDeleteClick(BudgetItem budget) {
                confirmDeleteBudget(budget);
            }
        });
        binding.rcvBudget.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rcvBudget.setAdapter(budgetAdapter);
    }

    private void setupEvents() {
        binding.btnAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddBudgetActivity.class);
            intent.putExtra("EXTRA_USER", user);
            intent.putExtra("EXTRA_BUDGET_MONTH", selectedMonth);
            intent.putExtra("EXTRA_BUDGET_YEAR", selectedYear);
            startActivity(intent);
        });
        binding.btnBudgetPrevPeriod.setOnClickListener(v -> changePeriod(-1));
        binding.btnBudgetNextPeriod.setOnClickListener(v -> changePeriod(1));
    }

    private void observeBudgets() {
        if (user == null) return;

        budgetViewModel.getBudgetUI(user.getUserId(), selectedMonth, selectedYear)
                .observe(getViewLifecycleOwner(), list -> {
                    budgetAdapter.setData(list);

                    long totalLeft = 0;
                    long totalLimit = 0;
                    long totalSpent = 0;
                    if (list != null) {
                        for (BudgetItem item : list) {
                            totalLeft += item.limitAmount - item.spentAmount;
                            totalLimit += item.limitAmount;
                            totalSpent += item.spentAmount;
                        }
                    }

                    int percent = totalLimit == 0 ? 0 : (int) ((totalSpent * 100) / totalLimit);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                    binding.tvBudgetLeft.setText(formatter.format(totalLeft));
                    binding.tvBudgetSpentPercent.setText("Bạn đã chi tiêu " + percent + "% hạn mức");
                    binding.tvBudgetCardMonth.setText("Tháng " + selectedMonth + "/" + selectedYear + " bạn còn lại");
                    updatePeriodText();
                });
    }

    private void changePeriod(int monthOffset) {
        selectedMonth += monthOffset;
        if (selectedMonth < 1) {
            selectedMonth = 12;
            selectedYear--;
        } else if (selectedMonth > 12) {
            selectedMonth = 1;
            selectedYear++;
        }

        updatePeriodText();
        if (user != null) {
            budgetViewModel.getBudgetUI(user.getUserId(), selectedMonth, selectedYear);
        }
    }

    private void updatePeriodText() {
        binding.tvBudgetMonth.setText("Tháng " + selectedMonth + "/" + selectedYear);
    }

    private void openEditBudget(BudgetItem budget) {
        Intent intent = new Intent(requireContext(), EditBudgetActivity.class);
        intent.putExtra("EXTRA_BUDGET_ID", budget.budgetId);
        intent.putExtra("EXTRA_CATEGORY_ID", budget.categoryId);
        intent.putExtra("EXTRA_LIMIT_AMOUNT", budget.limitAmount);
        startActivity(intent);
    }

    private void confirmDeleteBudget(BudgetItem budget) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa ngân sách")
                .setMessage("Bạn có chắc muốn xóa ngân sách " + budget.categoryName + "?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> deleteBudget(budget.budgetId))
                .show();
    }

    private void deleteBudget(int budgetId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            int result = budgetManager.deleteById(budgetId);
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                if (result <= 0) {
                    new AlertDialog.Builder(requireContext())
                            .setMessage("Xóa ngân sách thất bại!")
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
