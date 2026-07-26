package com.example.myapplication.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.databinding.ItemTransactionHomeBinding;
import com.example.myapplication.utils.CurrencyUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeTransactionAdapter extends RecyclerView.Adapter<HomeTransactionAdapter.HomeViewHolder> {

    private List<TransactionDTO> dtoList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TransactionDTO dto);
    }

    public HomeTransactionAdapter(List<TransactionDTO> dtoList, OnItemClickListener listener) {
        this.dtoList = dtoList;
        this.listener = listener;
    }

    public void setDtoList(List<TransactionDTO> dtoList) {
        this.dtoList = dtoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionHomeBinding binding = ItemTransactionHomeBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new HomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        TransactionDTO dto = dtoList.get(position);
        holder.bind(dto, listener);
    }

    @Override
    public int getItemCount() {
        return dtoList == null ? 0 : dtoList.size();
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {

        private final ItemTransactionHomeBinding binding;

        public HomeViewHolder(@NonNull ItemTransactionHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TransactionDTO dto, OnItemClickListener listener) {
            if (dto == null || dto.getTransaction() == null) return;

            Context context = itemView.getContext();

            // 1. Tên danh mục
            String categoryName = dto.getCategoryName() != null ? dto.getCategoryName() : "Không xác định";
            binding.tvCategoryName.setText(categoryName);

            // 2. Icon danh mục
            if (dto.getIconName() != null && !dto.getIconName().isEmpty()) {
                int iconRes = context.getResources().getIdentifier(
                        dto.getIconName(),
                        "drawable",
                        context.getPackageName()
                );
                binding.imgCategoryIcon.setImageResource(iconRes != 0 ? iconRes : R.drawable.ic_category_tag);
            } else {
                binding.imgCategoryIcon.setImageResource(R.drawable.ic_category_tag);
            }

            // 3. XỬ LÝ MÀU SẮC
            int categoryColor;
            try {
                categoryColor = Color.parseColor(dto.getCategoryColor());
            } catch (Exception e) {
                categoryColor = ContextCompat.getColor(context, R.color.blueApp);
            }

            // Nền: Giữ NGUYÊN BẢN 100% màu gốc của Category
            binding.imgCategoryIcon.setBackgroundTintList(ColorStateList.valueOf(categoryColor));

            // Icon: Bỏ hoàn toàn tint từ code Java để dùng màu đã định nghĩa trong file XML của Icon
            ImageViewCompat.setImageTintList(binding.imgCategoryIcon, null);

            // 4. Số tiền (+ Thu / - Chi)
            long absAmount = Math.abs(dto.getTransaction().getAmount());
            boolean isExpense = "EXPENSE".equalsIgnoreCase(dto.getTransaction().getType())
                    || dto.getTransaction().getAmount() < 0;

            if (isExpense) {
                binding.tvAmount.setText("-" + CurrencyUtils.formatCurrency(absAmount));
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            } else {
                binding.tvAmount.setText("+" + CurrencyUtils.formatCurrency(absAmount));
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.blueApp));
            }

            // 5. Ghi chú & Ngày tháng
            String note = dto.getTransaction().getNote();
            String formattedDate = formatDateString(dto.getTransaction().getTransactionDate());

            if (note != null && !note.trim().isEmpty()) {
                binding.tvSubTitle.setText(note.trim() + "  •  " + formattedDate);
            } else {
                binding.tvSubTitle.setText(formattedDate);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(dto);
            });
        }

        /**
         * Định dạng ngày tháng thông minh: Hôm nay, Hôm qua, hoặc dd ThMM
         */
        private String formatDateString(long timestamp) {
            Calendar itemCal = Calendar.getInstance();
            itemCal.setTimeInMillis(timestamp);

            Calendar today = Calendar.getInstance();

            if (itemCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    itemCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return "Hôm nay";
            }

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);
            if (itemCal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                    itemCal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                return "Hôm qua";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd 'Th'MM", new Locale("vi", "VN"));
            return sdf.format(itemCal.getTime());
        }
    }
}