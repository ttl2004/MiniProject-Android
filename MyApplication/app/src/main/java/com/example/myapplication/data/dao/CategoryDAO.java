package com.example.myapplication.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.data.entity.Category;

import java.util.List;

@Dao
public interface CategoryDAO {
    @Insert
    long insert(Category category);

    @Insert
    void insertALL(List<Category> categoryList);

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getALL();

    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    Category getCategoryById(long categoryId);

    @Query("SELECT * FROM categories WHERE type = :type")
    LiveData<List<Category>> getCategoriesByType(String type);
}
