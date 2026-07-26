package com.example.myapplication.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.data.entity.Transaction;
import com.example.myapplication.manager.TransactionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeViewModel extends AndroidViewModel {

    private TransactionManager transactionManager;
    private final MutableLiveData<Integer> currentUserId = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        transactionManager = new TransactionManager(application);
    }

    public void setUserId(int userId) {
        currentUserId.setValue(userId);
    }

    // LiveData tự động lấy danh sách giao dịch DTO trong tháng hiện tại
    public final LiveData<List<TransactionDTO>> currentMonthTransactions = Transformations.switchMap(
            currentUserId,
            userId -> {
                if (userId == null) {
                    return new MutableLiveData<>(new ArrayList<>());
                }

                // Lấy khoảng thời gian từ đầu tháng đến cuối tháng hiện tại
                Calendar cal = Calendar.getInstance();

                // Đầu tháng hiện tại: 00:00:00.000
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long startOfMonth = cal.getTimeInMillis();

                // Cuối tháng hiện tại: 23:59:59.999
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                long endOfMonth = cal.getTimeInMillis();

                return transactionManager.getTransactionsByRange(userId, startOfMonth, endOfMonth);
            }
    );

    // Xóa giao dịch từ Home
    public void deleteTransaction(Transaction transaction) {
        if (transaction != null && currentUserId.getValue() != null
                && transaction.getUserId() == currentUserId.getValue()) {
            Executors.newSingleThreadExecutor().execute(() -> {
                transactionManager.delete(transaction);
            });
        }
    }
}