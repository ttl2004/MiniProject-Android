package com.example.myapplication.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.entity.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private List<Category> categoryList;
    private OnItemClickListener listener;

    // true = item_category_horizontal
    private boolean selectable;

    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    // Constructor dùng cho CategoryFragment
    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
        this.selectable = false;
    }

    // Constructor dùng cho AddTransaction
    public CategoryAdapter(List<Category> categoryList,
                           OnItemClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
        this.selectable = true;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public Category getSelectedCategory() {
        if (categoryList == null || categoryList.isEmpty()) {
            return null;
        }
        if (selectedPosition < 0 || selectedPosition >= categoryList.size()) {
            selectedPosition = 0;
        }
        return categoryList.get(selectedPosition);
    }

    // --- BỔ SUNG: Hàm chọn danh mục từ bên ngoài (Dùng cho Edit Mode) ---
    public void setSelectedCategory(Category category) {
        if (categoryList == null || category == null) return;

        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == category.getId()) {
                setSelectedPosition(i);
                break;
            }
        }
    }

    // --- BỔ SUNG: Hàm đổi vị trí chọn và làm mới UI ---
    public void setSelectedPosition(int position) {
        if (position >= 0 && categoryList != null && position < categoryList.size()) {
            int oldPosition = selectedPosition;
            selectedPosition = position;

            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
        }
    }

    public void selectCategoryById(int categoryId) {
        if (categoryList == null) return;

        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == categoryId) {
                int old = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(old);
                notifyItemChanged(selectedPosition);
                return;
            }
        }
    }
    public void updateData(List<Category> newCategories) {
        this.categoryList = newCategories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layout;

        if (selectable) {
            layout = R.layout.item_category_horizontal;
        } else {
            layout = R.layout.item_category;
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {

        Category category = categoryList.get(position);

        holder.categoryName.setText(category.getName());

        Context context = holder.itemView.getContext();

        // icon
        String iconName = category.getIcon() == null ? "" : category.getIcon();
        int iconRes = context.getResources().getIdentifier(
                iconName,
                "drawable",
                context.getPackageName());

        if (iconRes != 0) {
            holder.categoryIcon.setImageResource(iconRes);
        } else {
            holder.categoryIcon.setImageResource(R.drawable.ic_category_tag);
        }

        // màu
        int color;

        try {
            color = Color.parseColor(category.getColor());
        } catch (Exception e) {
            color = ContextCompat.getColor(context, R.color.blueApp);
        }

        holder.categoryIcon.setBackgroundTintList(ColorStateList.valueOf(color));

        // Chỉ màn AddTransaction mới có hiệu ứng chọn
        if (selectable) {

            if (position == selectedPosition) {
                holder.viewSelected.setVisibility(View.VISIBLE);
            } else {
                holder.viewSelected.setVisibility(View.GONE);
            }

        } else {

            holder.categoryIcon.setBackgroundTintList(
                    ColorStateList.valueOf(color)
            );
        }

        holder.itemView.setOnClickListener(v -> {

            if (selectable) {

                int adapterPosition = holder.getBindingAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return;
                }

                int old = selectedPosition;
                selectedPosition = adapterPosition;

                if (old >= 0 && old < getItemCount()) {
                    notifyItemChanged(old);
                }
                notifyItemChanged(selectedPosition);
            }

            if (listener != null) {
                listener.onItemClick(category);
            }

        });
    }

    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }

    static class CategoryHolder extends RecyclerView.ViewHolder {

        ImageView categoryIcon;
        TextView categoryName;
        View viewSelected;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.tv_category_name);
            categoryIcon = itemView.findViewById(R.id.img_category);
            viewSelected = itemView.findViewById(R.id.view_selected);
        }
    }
}
