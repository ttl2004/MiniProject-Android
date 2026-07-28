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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionDetailAdapter extends RecyclerView.Adapter<TransactionDetailAdapter.ViewHolder> {

    public interface OnTransactionActionListener {
        void onEdit(TransactionDTO dto);
        void onDelete(TransactionDTO dto);
    }

    private List<TransactionDTO> dtoList;
    private final OnTransactionActionListener actionListener;

    public TransactionDetailAdapter(List<TransactionDTO> dtoList, OnTransactionActionListener actionListener) {
        this.dtoList = dtoList;
        this.actionListener = actionListener;
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

        // Tên Danh mục
        holder.binding.tvNote.setText(categoryName);

        // Ghi chú (nếu có)
        if (note != null && !note.trim().isEmpty()) {
            holder.binding.tvCategoryName.setText(note);
            holder.binding.tvCategoryName.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvCategoryName.setVisibility(View.GONE);
        }

        // Icon danh mục
        String iconName = dto.getIconName();
        if (iconName != null && !iconName.isEmpty()) {
            int iconResId = holder.itemView.getContext().getResources().getIdentifier(
                    iconName, "drawable", holder.itemView.getContext().getPackageName()
            );
            if (iconResId != 0) {
                holder.binding.imgCategoryIcon.setImageResource(iconResId);
            }
        }

        // Màu Icon
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

        // Định dạng tiền tệ
        long amount = Math.abs(transaction.getAmount());
        String formattedAmount = CurrencyUtils.formatCurrency(amount);

        if ("EXPENSE".equalsIgnoreCase(transaction.getType()) || transaction.getAmount() < 0) {
            holder.binding.tvAmount.setText("-" + formattedAmount);
            holder.binding.tvAmount.setTextColor(Color.parseColor("#D32F2F"));
        } else {
            holder.binding.tvAmount.setText("+" + formattedAmount);
            holder.binding.tvAmount.setTextColor(Color.parseColor("#388E3C"));
        }

        // Sự kiện Sửa & Xóa
        if (actionListener != null) {
            holder.binding.layoutActions.setVisibility(View.VISIBLE);
            holder.binding.tvTransactionTime.setVisibility(View.GONE);
            
            holder.binding.btnEdit.setOnClickListener(v -> {
                actionListener.onEdit(dto);
            });

            holder.binding.btnDelete.setOnClickListener(v -> {
                actionListener.onDelete(dto);
            });
        } else {
            holder.binding.layoutActions.setVisibility(View.GONE);
            holder.binding.tvTransactionTime.setVisibility(View.VISIBLE);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.binding.tvTransactionTime.setText(sdf.format(new Date(transaction.getTransactionDate())));
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