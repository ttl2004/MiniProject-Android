package com.example.myapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemTransactionGroupBinding;
import com.example.myapplication.ui.transaction.model.TransactionGroup;
import com.example.myapplication.utils.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public class TransactionGroupAdapter extends RecyclerView.Adapter<TransactionGroupAdapter.ViewHolder> {

    private List<TransactionGroup> groupList;
    private final TransactionDetailAdapter.OnTransactionActionListener actionListener;

    // RecycledViewPool dùng chung cho tất cả các sub-RecyclerView để tái sử dụng ViewHolder con
    private final RecyclerView.RecycledViewPool sharedViewPool = new RecyclerView.RecycledViewPool();

    public TransactionGroupAdapter(List<TransactionGroup> groupList, TransactionDetailAdapter.OnTransactionActionListener actionListener) {
        this.groupList = groupList != null ? groupList : new ArrayList<>();
        this.actionListener = actionListener;
    }

    public void setGroupList(List<TransactionGroup> groupList) {
        this.groupList = groupList != null ? groupList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionGroupBinding binding = ItemTransactionGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding, actionListener, sharedViewPool);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionGroup group = groupList.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return groupList != null ? groupList.size() : 0;
    }

    // =========================================================================
    // VIEWHOLDER TỐI ƯU (Khởi tạo LayoutManager & Adapter 1 lần duy nhất)
    // =========================================================================
    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTransactionGroupBinding binding;
        private final TransactionDetailAdapter detailAdapter;

        public ViewHolder(ItemTransactionGroupBinding binding,
                          TransactionDetailAdapter.OnTransactionActionListener actionListener,
                          RecyclerView.RecycledViewPool viewPool) {
            super(binding.getRoot());
            this.binding = binding;

            // 1. Tạo LayoutManager 1 lần duy nhất
            LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext());
            binding.rvSubTransactions.setLayoutManager(layoutManager);

            // Tối ưu hiệu năng cho Nested RecyclerView
            binding.rvSubTransactions.setHasFixedSize(true);
            binding.rvSubTransactions.setNestedScrollingEnabled(false);
            binding.rvSubTransactions.setRecycledViewPool(viewPool); // Chia sẻ view pool giữa các nhóm

            // 2. Tạo Adapter 1 lần duy nhất với danh sách rỗng ban đầu
            detailAdapter = new TransactionDetailAdapter(new ArrayList<>(), actionListener);
            binding.rvSubTransactions.setAdapter(detailAdapter);
        }

        public void bind(TransactionGroup group) {
            // Bind Header Data
            binding.tvGroupDate.setText(group.getDateHeader());

            long total = group.getTotalAmount();
            if (total < 0) {
                String formattedTotal = "-" + CurrencyUtils.formatCurrency(Math.abs(total));
                binding.tvGroupTotalAmount.setText(formattedTotal);
                binding.tvGroupTotalAmount.setTextColor(Color.parseColor("#D32F2F"));
            } else if (total > 0) {
                String formattedTotal = "+" + CurrencyUtils.formatCurrency(total);
                binding.tvGroupTotalAmount.setText(formattedTotal);
                binding.tvGroupTotalAmount.setTextColor(Color.parseColor("#388E3C"));
            } else {
                binding.tvGroupTotalAmount.setText("0đ");
                binding.tvGroupTotalAmount.setTextColor(Color.parseColor("#757575"));
            }

            // 3. Chỉ CẬP NHẬT DỮ LIỆU cho Adapter con, không khởi tạo mới
            detailAdapter.setTransactionList(group.getTransactions());
        }
    }
}