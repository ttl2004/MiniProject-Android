package com.example.myapplication.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.dao.CategoryDAO;
import com.example.myapplication.data.entity.Category;

import java.util.List;

public class CategoryRepository {
    private final CategoryDAO categoryDAO;

    public CategoryRepository(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public long insert(Category category) {
        return categoryDAO.insert(category);
    }

    public void insertALL(List<Category> categories) {
        categoryDAO.insertALL(categories);
    }

    public LiveData<List<Category>> getALL(){
        return categoryDAO.getALL();
    }

    public Category getCategoryById(long categoryId) {
        return categoryDAO.getCategoryById(categoryId);
    }

    public LiveData<List<Category>> getCategoriesByType(String type) {
        return categoryDAO.getCategoriesByType(type);
    }
}
