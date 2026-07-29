package com.example.myapplication.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.data.dto.BudgetItem;
import com.example.myapplication.data.entity.Budget;

import java.util.List;

@Dao
public interface BudgetDAO {
    @Insert
    long insert(Budget budget);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND categoryId = :categoryId AND month = :month AND year = :year LIMIT 1")
    Budget getBudgetByCategoryPeriod(int userId, int categoryId, int month, int year);

    @Query("SELECT * FROM budgets")
    LiveData<List<Budget>> getAll();

    @Query(
            "SELECT " +
                    "b.id AS budgetId, " +
                    "c.id AS categoryId, " +
                    "c.name AS categoryName, " +
                    "c.icon AS icon, " +
                    "c.color AS color, " +
                    "b.limitAmount AS limitAmount, " +
                    "IFNULL(SUM(t.amount), 0) AS spentAmount, " +
                    "0 AS percent " +
                    "FROM budgets b " +
                    "JOIN categories c ON b.categoryId = c.id " +
                    "LEFT JOIN transactions t " +
                    "ON t.categoryId = b.categoryId " +
                    "AND t.type = 'EXPENSE' " +
                    "AND strftime('%m', t.transactionDate / 1000, 'unixepoch') = :monthText " +
                    "AND strftime('%Y', t.transactionDate / 1000, 'unixepoch') = :year " +
                    "WHERE b.userId = :userId " +
                    "AND b.month = :month " +
                    "AND b.year = :yearInt " +
                    "GROUP BY b.id"
    )
    LiveData<List<BudgetItem>>getBudgetItems(int userId, int month, int yearInt, String monthText, String year);

    @Query("UPDATE budgets SET categoryId = :categoryId, limitAmount = :limitAmount, updatedAt = :updatedAt WHERE id = :budgetId")
    int updateBudget(int budgetId, int categoryId, long limitAmount, long updatedAt);

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    int deleteById(int budgetId);
}
