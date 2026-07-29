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
        if (categoryList == null || categoryList.isEmpty() || selectedPosition < 0 || selectedPosition >= categoryList.size()) {
            return null;
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

            if (oldPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldPosition);
            }
            notifyItemChanged(selectedPosition);
        }
    }

    public void selectCategoryById(int categoryId) {
        if (categoryList == null) return;

        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == categoryId) {
                int old = selectedPosition;
                selectedPosition = i;
                if (old != RecyclerView.NO_POSITION) {
                    notifyItemChanged(old);
                }
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
        int layout = selectable ? R.layout.item_category_horizontal : R.layout.item_category;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        Category category = categoryList.get(position);

        if (holder.categoryName != null) {
            holder.categoryName.setText(category.getName());
        }

        Context context = holder.itemView.getContext();

        // Icon
        if (holder.categoryIcon != null) {
            int iconRes = context.getResources().getIdentifier(
                    category.getIcon(),
                    "drawable",
                    context.getPackageName());

            if (iconRes != 0) {
                holder.categoryIcon.setImageResource(iconRes);
            } else {
                holder.categoryIcon.setImageResource(R.drawable.ic_category_tag);
            }

            // Màu
            int color;
            try {
                color = Color.parseColor(category.getColor());
            } catch (Exception e) {
                color = ContextCompat.getColor(context, R.color.blueApp);
            }

            holder.categoryIcon.setBackgroundTintList(ColorStateList.valueOf(color));
        }

        // 🟢 FIX BUG-18: Kiểm tra null an toàn tuyệt đối cho viewSelected
        if (holder.viewSelected != null) {
            if (selectable && position == selectedPosition) {
                holder.viewSelected.setVisibility(View.VISIBLE);
            } else {
                holder.viewSelected.setVisibility(View.GONE);
            }
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION || categoryList == null || pos >= categoryList.size()) {
                return; // Guard
            }

            int old = selectedPosition;
            selectedPosition = pos;

            if (old != RecyclerView.NO_POSITION) {
                notifyItemChanged(old);
            }
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onItemClick(categoryList.get(pos));
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
        View viewSelected; // Có thể null nếu layout không khai báo view_selected

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.tv_category_name);
            categoryIcon = itemView.findViewById(R.id.img_category);
            viewSelected = itemView.findViewById(R.id.view_selected);
        }
    }
}