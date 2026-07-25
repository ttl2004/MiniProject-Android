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

import java.util.List;

public class TransactionGroupAdapter extends RecyclerView.Adapter<TransactionGroupAdapter.ViewHolder> {

    private List<TransactionGroup> groupList;

    public TransactionGroupAdapter(List<TransactionGroup> groupList) {
        this.groupList = groupList;
    }

    public void setGroupList(List<TransactionGroup> groupList) {
        this.groupList = groupList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionGroupBinding binding = ItemTransactionGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionGroup group = groupList.get(position);

        holder.binding.tvGroupDate.setText(group.getDateHeader());

        long total = group.getTotalAmount();

        // Dùng CurrencyUtils để lấy format dùng dấu chấm '.'
        if (total < 0) {
            String formattedTotal = "-" + CurrencyUtils.formatCurrency(Math.abs(total));
            holder.binding.tvGroupTotalAmount.setText(formattedTotal);
            holder.binding.tvGroupTotalAmount.setTextColor(Color.parseColor("#D32F2F"));
        } else if (total > 0) {
            String formattedTotal = "+" + CurrencyUtils.formatCurrency(total);
            holder.binding.tvGroupTotalAmount.setText(formattedTotal);
            holder.binding.tvGroupTotalAmount.setTextColor(Color.parseColor("#388E3C"));
        } else {
            holder.binding.tvGroupTotalAmount.setText("0đ");
            holder.binding.tvGroupTotalAmount.setTextColor(Color.parseColor("#757575"));
        }

        TransactionDetailAdapter detailAdapter = new TransactionDetailAdapter(group.getTransactions());
        holder.binding.rvSubTransactions.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.binding.rvSubTransactions.setAdapter(detailAdapter);
    }

    @Override
    public int getItemCount() {
        return groupList != null ? groupList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTransactionGroupBinding binding;

        public ViewHolder(ItemTransactionGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}