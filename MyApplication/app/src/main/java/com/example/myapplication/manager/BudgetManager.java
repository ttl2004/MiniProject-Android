package com.example.myapplication.manager;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.dto.BudgetItem;
import com.example.myapplication.data.entity.Budget;
import com.example.myapplication.data.repository.BudgetRepository;

import java.util.List;

public class BudgetManager {
    private BudgetRepository budgetRepository;
    public BudgetManager(Context context){
        AppDatabase db = AppDatabase.getInstance(context);
        budgetRepository = new BudgetRepository(db.budgetDAO());
    }
    public long insert(Budget budget) {
        return budgetRepository.insert(budget);
    }
    public LiveData<List<BudgetItem>> getBudgetItems(int userId, int month, int year){
        return budgetRepository.getBudgetItems(userId,month,year);
    }

    public int updateBudget(int budgetId, int categoryId, int limitAmount) {
        return budgetRepository.updateBudget(budgetId, categoryId, limitAmount);
    }

    public int deleteById(int budgetId) {
        return budgetRepository.deleteById(budgetId);
    }
}
