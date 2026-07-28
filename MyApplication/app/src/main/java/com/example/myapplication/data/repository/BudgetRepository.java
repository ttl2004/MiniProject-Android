package com.example.myapplication.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myapplication.data.dao.BudgetDAO;
import com.example.myapplication.data.dto.BudgetItem;
import com.example.myapplication.data.entity.Budget;

import java.util.List;

public class BudgetRepository {

    private final BudgetDAO budgetDao;

    public BudgetRepository(BudgetDAO budgetDao) {
        this.budgetDao = budgetDao;
    }
    public long insert(Budget budget) {
        return budgetDao.insert(budget);
    }

    public Budget getBudgetByCategoryPeriod(int userId, int categoryId, int month, int year) {
        return budgetDao.getBudgetByCategoryPeriod(userId, categoryId, month, year);
    }

    public LiveData<List<BudgetItem>> getBudgetItems(int userId, int month, int year) {
        return budgetDao.getBudgetItems(
                userId,
                month,
                year,
                String.format("%02d", month),
                String.valueOf(year)
        );
    }

    public int updateBudget(int budgetId, int categoryId, int limitAmount) {
        return budgetDao.updateBudget(budgetId, categoryId, limitAmount, System.currentTimeMillis());
    }

    public int deleteById(int budgetId) {
        return budgetDao.deleteById(budgetId);
    }
}
