package com.example.myapplication.ui.budget;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.myapplication.data.dto.BudgetItem;
import com.example.myapplication.data.entity.Budget;
import com.example.myapplication.data.repository.BudgetRepository;
import com.example.myapplication.manager.BudgetManager;
import com.example.myapplication.manager.CategoryManager;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

public class BudgetViewModel extends AndroidViewModel {

    private BudgetManager budgetManager;
    private final MediatorLiveData<List<BudgetItem>> budgetUI = new MediatorLiveData<>();
    private LiveData<List<BudgetItem>> budgetSource;

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        budgetManager = new BudgetManager(application);
    }

    public LiveData<List<BudgetItem>> getBudgetUI(int userId) {

        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        return getBudgetUI(userId, month, year);
    }

    public LiveData<List<BudgetItem>> getBudgetUI(int userId, int month, int year) {
        if (budgetSource != null) {
            budgetUI.removeSource(budgetSource);
        }

        budgetSource = budgetManager.getBudgetItems(userId, month, year);

        budgetUI.addSource(budgetSource, list -> {
            if (list == null) return;

            for (BudgetItem item : list) {
                if (item.limitAmount == 0) {
                    item.percent = 0;
                } else {
                    item.percent = (int) ((item.spentAmount * 100) / item.limitAmount);
                }
            }

            budgetUI.setValue(list);
        });

        return budgetUI;
    }
}
