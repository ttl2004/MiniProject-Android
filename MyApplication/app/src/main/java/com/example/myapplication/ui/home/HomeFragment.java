package com.example.myapplication.ui.home;

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

import com.example.myapplication.R;
import com.example.myapplication.activity.addtransaction.AddTransactionActivity;
import com.example.myapplication.adapter.HomeTransactionAdapter;
import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.utils.CurrencyUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String ARG_USER = "EXTRA_USER";

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private HomeTransactionAdapter homeAdapter;
    private User currentUser;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable(ARG_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        if (currentUser != null) {
            viewModel.setUserId(currentUser.getUserId());
        }

        setupRecyclerView();
        setupEvents();
        observeData();
    }

    private void setupRecyclerView() {
        // Sử dụng HomeTransactionAdapter phẳng gọn cho trang chủ
        homeAdapter = new HomeTransactionAdapter(new ArrayList<>(), dto -> {
            if (dto == null || dto.getTransaction() == null) return;

            Intent intent = new Intent(requireContext(), AddTransactionActivity.class);
            intent.putExtra("EXTRA_USER", currentUser);
            intent.putExtra("EXTRA_TRANSACTION", dto.getTransaction());
            startActivity(intent);
        });

        binding.rvRecentTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecentTransactions.setAdapter(homeAdapter);
    }

    private void setupEvents() {
        // Chuyển tab sang màn hình Danh sách giao dịch khi nhấn "Xem tất cả"
        binding.btnViewAll.setOnClickListener(v -> {
            if (getActivity() != null) {
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.navTransaction); // Kiểm tra đúng id menu tab Transactions của bạn
                }
            }
        });
    }

    private void observeData() {
        viewModel.currentMonthTransactions.observe(getViewLifecycleOwner(), transactions -> {
            if (transactions == null || transactions.isEmpty()) {
                binding.tvTotalBalance.setText("0đ");
                binding.tvTotalIncome.setText("0đ");
                binding.tvTotalExpense.setText("0đ");

                homeAdapter.setDtoList(new ArrayList<>());

                // --- CẬP NHẬT Ở ĐÂY: Ẩn cả Card trắng bọc ngoài ---
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.cvRecentTransactions.setVisibility(View.GONE);
                return;
            }

            long totalIncome = 0;
            long totalExpense = 0;

            for (TransactionDTO dto : transactions) {
                if (dto.getTransaction() == null) continue;

                long absAmount = Math.abs(dto.getTransaction().getAmount());
                boolean isExpense = "EXPENSE".equalsIgnoreCase(dto.getTransaction().getType())
                        || dto.getTransaction().getAmount() < 0;

                if (isExpense) {
                    totalExpense += absAmount;
                } else {
                    totalIncome += absAmount;
                }
            }

            long balance = totalIncome - totalExpense;

            // Hiển thị tổng thu chi và số dư
            binding.tvTotalIncome.setText(CurrencyUtils.formatCurrency(totalIncome));
            binding.tvTotalExpense.setText(CurrencyUtils.formatCurrency(totalExpense));

            if (balance >= 0) {
                binding.tvTotalBalance.setText("+" + CurrencyUtils.formatCurrency(balance));
            } else {
                binding.tvTotalBalance.setText("-" + CurrencyUtils.formatCurrency(Math.abs(balance)));
            }

            // Sắp xếp thời gian giảm dần (mới nhất lên đầu)
            List<TransactionDTO> sortedList = new ArrayList<>(transactions);
            Collections.sort(sortedList, (o1, o2) -> Long.compare(
                    o2.getTransaction().getTransactionDate(),
                    o1.getTransaction().getTransactionDate()
            ));

            // Chỉ lấy tối đa 5 giao dịch gần nhất
            List<TransactionDTO> top5List = sortedList.subList(0, Math.min(sortedList.size(), 5));

            homeAdapter.setDtoList(top5List);

            // --- CẬP NHẬT Ở ĐÂY: Hiện Card trắng và ẩn text thông báo trống ---
            binding.tvEmpty.setVisibility(View.GONE);
            binding.cvRecentTransactions.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}