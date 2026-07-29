package com.example.myapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.data.entity.Transaction;
import com.example.myapplication.databinding.ItemTransactionDetailBinding;
import com.example.myapplication.utils.CurrencyUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        this.dtoList = dtoList != null ? dtoList : new ArrayList<>();
        this.actionListener = actionListener;
    }

    public void setDtoList(List<TransactionDTO> dtoList) {
        this.dtoList = dtoList != null ? dtoList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Alias method hỗ trợ tương thích tốt với TransactionGroupAdapter
    public void setTransactionList(List<TransactionDTO> dtoList) {
        setDtoList(dtoList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionDetailBinding binding = ItemTransactionDetailBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding, actionListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionDTO dto = dtoList.get(position);
        holder.bind(dto);
    }

    @Override
    public int getItemCount() {
        return dtoList != null ? dtoList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTransactionDetailBinding binding;

        public ViewHolder(ItemTransactionDetailBinding binding,
                          OnTransactionActionListener actionListener,
                          TransactionDetailAdapter adapter) {
            super(binding.getRoot());
            this.binding = binding;

            // Xử lý OnClick an toàn với getBindingAdapterPosition()
            if (actionListener != null) {
                binding.btnEdit.setOnClickListener(v -> {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && pos < adapter.dtoList.size()) {
                        actionListener.onEdit(adapter.dtoList.get(pos));
                    }
                });

                binding.btnDelete.setOnClickListener(v -> {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && pos < adapter.dtoList.size()) {
                        actionListener.onDelete(adapter.dtoList.get(pos));
                    }
                });
            }
        }

        public void bind(TransactionDTO dto) {
            Transaction transaction = dto.getTransaction();

            String note = transaction.getNote();
            String categoryName = (dto.getCategoryName() != null && !dto.getCategoryName().isEmpty())
                    ? dto.getCategoryName()
                    : "Khác";

            // Tên Danh mục
            binding.tvNote.setText(categoryName);

            // Ghi chú (nếu có)
            if (note != null && !note.trim().isEmpty()) {
                binding.tvCategoryName.setText(note);
                binding.tvCategoryName.setVisibility(View.VISIBLE);
            } else {
                binding.tvCategoryName.setVisibility(View.GONE);
            }

            // Icon danh mục
            String iconName = dto.getIconName();
            if (iconName != null && !iconName.isEmpty()) {
                int iconResId = itemView.getContext().getResources().getIdentifier(
                        iconName, "drawable", itemView.getContext().getPackageName()
                );
                if (iconResId != 0) {
                    binding.imgCategoryIcon.setImageResource(iconResId);
                }
            }

            // Màu Icon
            String colorHex = dto.getCategoryColor();
            if (colorHex != null && !colorHex.isEmpty()) {
                try {
                    int parsedColor = Color.parseColor(colorHex);
                    if (binding.imgCategoryIcon.getBackground() != null) {
                        DrawableCompat.setTint(
                                binding.imgCategoryIcon.getBackground().mutate(),
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
                binding.tvAmount.setText("-" + formattedAmount);
                binding.tvAmount.setTextColor(Color.parseColor("#D32F2F"));
            } else {
                binding.tvAmount.setText("+" + formattedAmount);
                binding.tvAmount.setTextColor(Color.parseColor("#388E3C"));
            }

            // Hiển thị nút sửa/xóa hoặc thời gian giao dịch
            // Lưu ý: Listener đã được gán 1 lần duy nhất trong Constructor của ViewHolder
            if (binding.layoutActions != null && binding.tvTransactionTime != null) {
                if (binding.btnEdit.getVisibility() != View.GONE) {
                    binding.layoutActions.setVisibility(View.VISIBLE);
                    binding.tvTransactionTime.setVisibility(View.GONE);
                } else {
                    binding.layoutActions.setVisibility(View.GONE);
                    binding.tvTransactionTime.setVisibility(View.VISIBLE);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    binding.tvTransactionTime.setText(sdf.format(new Date(transaction.getTransactionDate())));
                }
            }
        }
    }
}