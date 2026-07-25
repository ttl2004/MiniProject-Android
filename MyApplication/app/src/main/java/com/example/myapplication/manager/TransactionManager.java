package com.example.myapplication.manager;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.entity.Category;
import com.example.myapplication.data.entity.Transaction;
import com.example.myapplication.data.repository.CategoryRepository;
import com.example.myapplication.data.repository.TransactionRepository;

import java.util.List;

public class TransactionManager {
    private TransactionRepository transactionRepository;
    private CategoryRepository categoryRepository;

    public TransactionManager(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        transactionRepository = new TransactionRepository(db.transactionDAO());
        categoryRepository = new CategoryRepository(db.categoryDAO());
    }

    public long insert(Transaction transaction) {
        return transactionRepository.insert(transaction);
    }

    public LiveData<List<Category>> getALLCategories() {
        return categoryRepository.getALL();
    }

}
