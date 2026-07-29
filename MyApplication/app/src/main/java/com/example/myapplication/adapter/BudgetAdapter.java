package com.example.myapplication.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.dto.BudgetItem;
import com.example.myapplication.utils.BudgetCalculationUtils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private List<BudgetItem> budgetList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BudgetItem budget);
        void onEditClick(BudgetItem budget);
        void onDeleteClick(BudgetItem budget);
    }

    public BudgetAdapter(List<BudgetItem> budgetList, OnItemClickListener listener) {
        this.budgetList = budgetList;
        this.listener = listener;
    }

    public void setData(List<BudgetItem> list) {
        this.budgetList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {

        BudgetItem budget = budgetList.get(position);


        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));


        holder.tvName.setText(budget.categoryName);

        long left = BudgetCalculationUtils.safeSubtract(budget.limitAmount, budget.spentAmount);
        String amountText = "Còn " + formatter.format(left) + " / " + formatter.format(budget.limitAmount);
        holder.tvAmount.setText(amountText);

        int percent = BudgetCalculationUtils.calculateDisplayPercent(budget.spentAmount, budget.limitAmount);

        holder.tvPercent.setText(percent + "%");

        holder.progressBar.setProgress(Math.min(percent, 100));

        int statusColor = getStatusColor(percent);
        holder.tvPercent.setTextColor(statusColor);
        holder.progressBar.setProgressTintList(ColorStateList.valueOf(statusColor));
        holder.progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6E6E6")));

        Context context = holder.itemView.getContext();
        bindCategoryIcon(holder, context, budget);
        bindWarning(holder, percent, statusColor);

        // ===== Click =====
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(budget);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(budget);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(budget);
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetList == null ? 0 : budgetList.size();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {

        FrameLayout layoutIcon;
        ImageView imgIcon, btnEdit, btnDelete;
        TextView tvName, tvPercent, tvAmount, tvWarning;
        ProgressBar progressBar;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutIcon = itemView.findViewById(R.id.layout_budget_icon);
            imgIcon = itemView.findViewById(R.id.img_budget_icon);
            btnEdit = itemView.findViewById(R.id.btn_edit_budget);
            btnDelete = itemView.findViewById(R.id.btn_delete_budget);
            tvName = itemView.findViewById(R.id.tv_budget_name);
            tvPercent = itemView.findViewById(R.id.tv_budget_percent);
            tvAmount = itemView.findViewById(R.id.tv_budget_amount);
            tvWarning = itemView.findViewById(R.id.tv_budget_warning);
            progressBar = itemView.findViewById(R.id.progress_budget);
        }
    }

    private void bindCategoryIcon(BudgetViewHolder holder, Context context, BudgetItem budget) {
        String iconName = budget.icon == null ? "" : budget.icon;
        int iconRes = context.getResources().getIdentifier(
                iconName,
                "drawable",
                context.getPackageName()
        );

        holder.imgIcon.setImageResource(iconRes == 0 ? R.drawable.ic_category_tag : iconRes);

        try {
            holder.layoutIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(budget.color)));
        } catch (Exception e) {
            holder.layoutIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8F3EA")));
        }
    }

    private void bindWarning(BudgetViewHolder holder, int percent, int statusColor) {
        if (percent < 80) {
            holder.tvWarning.setVisibility(View.GONE);
            return;
        }

        holder.tvWarning.setVisibility(View.VISIBLE);
        holder.tvWarning.setTextColor(statusColor);

        if (percent > 100) {
            holder.tvWarning.setText("ĐÃ VƯỢT HẠN MỨC!");
            holder.tvWarning.setBackgroundResource(R.drawable.bg_budget_warning_danger);
        } else if (percent == 100) {
            holder.tvWarning.setText("ĐÃ HẾT HẠN MỨC!");
            holder.tvWarning.setBackgroundResource(R.drawable.bg_budget_warning_danger);
        } else if (percent >= 90) {
            holder.tvWarning.setText("SẮP VƯỢT HẠN MỨC");
            holder.tvWarning.setBackgroundResource(R.drawable.bg_budget_warning_soft);
        } else {
            holder.tvWarning.setText("SẮP CHẠM HẠN MỨC");
            holder.tvWarning.setBackgroundResource(R.drawable.bg_budget_warning_soft);
        }
    }

    private int getStatusColor(int percent) {
        if (percent >= 100) return Color.parseColor("#D32F2F");
        if (percent >= 80) return Color.parseColor("#EF8F3A");
        if (percent >= 60) return Color.parseColor("#1976D2");
        return Color.parseColor("#2E7D32");
    }

}
