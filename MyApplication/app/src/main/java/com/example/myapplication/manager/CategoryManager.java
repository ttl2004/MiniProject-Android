package com.example.myapplication.manager;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.entity.Category;
import com.example.myapplication.data.repository.CategoryRepository;

import java.util.List;

public class CategoryManager {
    private CategoryRepository categoryRepository;

    public CategoryManager(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        categoryRepository = new CategoryRepository(db.categoryDAO());
    }

    public long insert(Category category) {
        return categoryRepository.insert(category);
    }
    public void insertALL(List<Category> categoryList) {
       categoryRepository.insertALL(categoryList);
    }

    public LiveData<List<Category>> getALL() {
        return categoryRepository.getALL();
    }
}
