package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemMonthChipBinding;
import com.example.myapplication.ui.month.MonthModel;

import java.util.ArrayList;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

    public interface OnMonthClickListener {
        void onMonthClick(MonthModel monthModel, int position);
    }

    private final List<MonthModel> monthList;
    private final OnMonthClickListener listener;
    private int selectedPosition = 0;

    public MonthAdapter(List<MonthModel> monthList, OnMonthClickListener listener) {
        this.monthList = (monthList != null) ? monthList : new ArrayList<>();
        this.listener = listener;
    }

    // THÊM HÀM NÀY: Để cập nhật danh sách tháng mới khi chuyển năm
    public void setMonthList(List<MonthModel> newMonthList) {
        this.monthList.clear();
        if (newMonthList != null) {
            this.monthList.addAll(newMonthList);
        }
        this.selectedPosition = 0; // Reset về tháng mới nhất (đầu danh sách) khi đổi năm
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMonthChipBinding binding = ItemMonthChipBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new MonthViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        MonthModel monthModel = monthList.get(position);
        holder.binding.tvMonthName.setText(monthModel.getDisplayName());

        // Sử dụng setSelected để Selector tự đổi màu nền và màu chữ
        boolean isSelected = (position == selectedPosition);
        holder.binding.tvMonthName.setSelected(isSelected);

        holder.itemView.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION || currentPos == selectedPosition) return;

            int previousSelected = selectedPosition;
            selectedPosition = currentPos;

            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onMonthClick(monthList.get(currentPos), selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }

    public void setSelectedPosition(int position) {
        if (position >= 0 && position < monthList.size()) {
            int oldPos = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
        }
    }

    static class MonthViewHolder extends RecyclerView.ViewHolder {
        final ItemMonthChipBinding binding;

        public MonthViewHolder(ItemMonthChipBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}