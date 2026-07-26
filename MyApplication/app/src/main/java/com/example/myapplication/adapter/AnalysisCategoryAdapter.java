package com.example.myapplication.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.dto.AnalysisCategoryDTO;

import java.text.DecimalFormat;
import java.util.List;

public class AnalysisCategoryAdapter extends RecyclerView.Adapter<AnalysisCategoryAdapter.ViewHolder> {

    private List<AnalysisCategoryDTO> list;
    private Context context;
    private DecimalFormat currencyFormat = new DecimalFormat("#,### đ");

    public AnalysisCategoryAdapter(Context context, List<AnalysisCategoryDTO> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<AnalysisCategoryDTO> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analysis_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnalysisCategoryDTO item = list.get(position);

        holder.tvCategoryName.setText(item.getCategoryName());
        holder.tvTotalAmount.setText(currencyFormat.format(item.getTotalAmount()));
        
        // Setup Percentage
        holder.tvPercentage.setText(String.format("%.1f%%", item.getPercentage()));
        holder.pbCategoryPercent.setProgress((int) item.getPercentage());

        // Setup Icon and Color
        if (item.getIconName() != null && !item.getIconName().isEmpty()) {
            int iconResId = context.getResources().getIdentifier(item.getIconName(), "drawable", context.getPackageName());
            if (iconResId != 0) {
                holder.ivCategoryIcon.setImageResource(iconResId);
            } else {
                holder.ivCategoryIcon.setImageResource(R.drawable.ic_category_eat); // Default icon
            }
        }

        if (item.getCategoryColor() != null && !item.getCategoryColor().isEmpty()) {
            try {
                int color = Color.parseColor(item.getCategoryColor());
                holder.cvCategoryIcon.setCardBackgroundColor(color);
                holder.pbCategoryPercent.setProgressTintList(ColorStateList.valueOf(color));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvCategoryIcon;
        ImageView ivCategoryIcon;
        TextView tvCategoryName;
        ProgressBar pbCategoryPercent;
        TextView tvTotalAmount;
        TextView tvPercentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvCategoryIcon = itemView.findViewById(R.id.cv_category_icon);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            pbCategoryPercent = itemView.findViewById(R.id.pb_category_percent);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
        }
    }
}
