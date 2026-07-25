package com.example.myapplication.ui.category;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.manager.CategoryManager;
import com.example.myapplication.data.entity.Category;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryManager categoryManager;
    private LiveData<List<Category>> allCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryManager = new CategoryManager(application);
        allCategories = categoryManager.getALL();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }
}