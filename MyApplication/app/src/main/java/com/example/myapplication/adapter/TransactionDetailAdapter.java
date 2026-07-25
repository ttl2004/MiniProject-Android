package com.example.myapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.data.entity.Transaction;
import com.example.myapplication.databinding.ItemTransactionDetailBinding;
import com.example.myapplication.utils.CurrencyUtils;

import java.util.List;

public class TransactionDetailAdapter extends RecyclerView.Adapter<TransactionDetailAdapter.ViewHolder> {

    private List<TransactionDTO> dtoList;

    public TransactionDetailAdapter(List<TransactionDTO> dtoList) {
        this.dtoList = dtoList;
    }

    public void setDtoList(List<TransactionDTO> dtoList) {
        this.dtoList = dtoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionDetailBinding binding = ItemTransactionDetailBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionDTO dto = dtoList.get(position);
        Transaction transaction = dto.getTransaction();

        String note = transaction.getNote();
        String categoryName = (dto.getCategoryName() != null && !dto.getCategoryName().isEmpty())
                ? dto.getCategoryName()
                : "Khác";

        // 1. Luôn hiển thị Tên Category ở dòng trên (tvNote)
        holder.binding.tvNote.setText(categoryName);

        // 2. Kiểm tra Note để hiển thị ở dòng dưới (tvCategoryName)
        if (note != null && !note.trim().isEmpty()) {
            // Có Note: Hiển thị Note ở dòng dưới (chữ nhạt)
            holder.binding.tvCategoryName.setText(note);
            holder.binding.tvCategoryName.setVisibility(View.VISIBLE);
        } else {
            // Không có Note: Ẩn dòng dưới đi
            holder.binding.tvCategoryName.setVisibility(View.GONE);
        }

        // --- Các phần xử lý Icon, Màu nền & Số tiền bên dưới giữ nguyên ---
        String iconName = dto.getIconName();
        if (iconName != null && !iconName.isEmpty()) {
            int iconResId = holder.itemView.getContext().getResources().getIdentifier(
                    iconName, "drawable", holder.itemView.getContext().getPackageName()
            );
            if (iconResId != 0) {
                holder.binding.imgCategoryIcon.setImageResource(iconResId);
            }
        }

        String colorHex = dto.getCategoryColor();
        if (colorHex != null && !colorHex.isEmpty()) {
            try {
                int parsedColor = Color.parseColor(colorHex);
                if (holder.binding.imgCategoryIcon.getBackground() != null) {
                    androidx.core.graphics.drawable.DrawableCompat.setTint(
                            holder.binding.imgCategoryIcon.getBackground().mutate(),
                            parsedColor
                    );
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        long amount = Math.abs(transaction.getAmount());
        String formattedAmount = CurrencyUtils.formatCurrency(amount);

        if ("EXPENSE".equalsIgnoreCase(transaction.getType()) || transaction.getAmount() < 0) {
            holder.binding.tvAmount.setText("-" + formattedAmount);
            holder.binding.tvAmount.setTextColor(Color.parseColor("#D32F2F"));
        } else {
            holder.binding.tvAmount.setText("+" + formattedAmount);
            holder.binding.tvAmount.setTextColor(Color.parseColor("#388E3C"));
        }
    }
    @Override
    public int getItemCount() {
        return dtoList != null ? dtoList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTransactionDetailBinding binding;

        public ViewHolder(ItemTransactionDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}