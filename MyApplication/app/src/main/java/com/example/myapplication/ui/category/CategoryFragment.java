package com.example.myapplication.ui.category;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.databinding.FragmentCategoryBinding;
import com.example.myapplication.manager.CategoryManager;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    private FragmentCategoryBinding binding;
    private CategoryViewModel categoryViewModel;
    private CategoryAdapter categoryAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Cấu hình RecyclerView
        binding.rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        binding.rvCategories.setAdapter(categoryAdapter);

        // 2. Khởi tạo ViewModel
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // 3. Sự kiện Click chuyển Tab (Dùng tham số String)
        binding.btnExpense.setOnClickListener(v -> selectTab(CategoryManager.TYPE_EXPENSE));
        binding.btnIncome.setOnClickListener(v -> selectTab(CategoryManager.TYPE_INCOME));

        // 4. Mặc định hiển thị danh mục Chi tiêu khi vào màn hình
        selectTab(CategoryManager.TYPE_EXPENSE);
    }

    /**
     * Xử lý đổi style nút bấm và load dữ liệu theo loại (String: "EXPENSE" / "INCOME")
     */
    private void selectTab(String type) {
        if (CategoryManager.TYPE_EXPENSE.equals(type)) {
            // Nút Chi tiêu active
            binding.btnExpense.setBackgroundResource(R.drawable.bg_category_toggle_selected);
            binding.btnExpense.setTextColor(Color.WHITE);

            // Nút Thu nhập inactive
            binding.btnIncome.setBackgroundResource(android.R.color.transparent);
            binding.btnIncome.setTextColor(Color.parseColor("#757575"));
        } else {
            // Nút Thu nhập active
            binding.btnIncome.setBackgroundResource(R.drawable.bg_category_toggle_selected);
            binding.btnIncome.setTextColor(Color.WHITE);

            // Nút Chi tiêu inactive
            binding.btnExpense.setBackgroundResource(android.R.color.transparent);
            binding.btnExpense.setTextColor(Color.parseColor("#757575"));
        }

        // Lấy danh sách danh mục theo type (String) từ ViewModel
        categoryViewModel.getCategoriesByType(type).observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                categoryAdapter.updateData(categories);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Tránh leak memory khi dùng View Binding
    }
}