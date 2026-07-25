package com.example.myapplication.manager;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.data.AppDatabase;
import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.data.entity.Category;
import com.example.myapplication.data.entity.Transaction;
import com.example.myapplication.data.repository.CategoryRepository;
import com.example.myapplication.data.repository.TransactionRepository;

import java.util.ArrayList;
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

    public LiveData<List<TransactionDTO>> getTransactionsByRange(int userID, long startDate, long endDate) {
        LiveData<List<Transaction>> rawLiveData = transactionRepository.getTransactionsByUserIdAndRange(userID,startDate, endDate);

        // Biến đổi LiveData<List<Transaction>> -> LiveData<List<TransactionDTO>>
        return Transformations.map(rawLiveData, transactions -> {
            List<TransactionDTO> dtoList = new ArrayList<>();
            if (transactions == null) return dtoList;

            for (Transaction t : transactions) {
                Category cat = categoryRepository.getCategoryById(t.getCategoryId());

                String categoryName = (cat != null) ? cat.getName() : "Khác";
                String iconName = (cat != null) ? cat.getIcon() : "";
                String categoryColor = (cat != null) ? cat.getColor() : "#E0E0E0"; // Màu xám mặc định nếu null

                dtoList.add(new TransactionDTO(t, categoryName, iconName, categoryColor));
            }
            return dtoList;
        });
    }
}
