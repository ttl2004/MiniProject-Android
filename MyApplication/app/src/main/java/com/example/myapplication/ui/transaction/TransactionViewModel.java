package com.example.myapplication.ui.transaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.data.dto.TransactionDTO; // Import DTO
import com.example.myapplication.data.entity.Transaction;
import com.example.myapplication.manager.TransactionManager;
import com.example.myapplication.ui.month.MonthModel;
import com.example.myapplication.ui.transaction.model.TransactionGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionViewModel extends AndroidViewModel {

    private  TransactionManager transactionManager;

    // 1. Khai báo MutableLiveData cho userId và selectedMonth
    private final MutableLiveData<Integer> currentUserId = new MutableLiveData<>();
    private final MutableLiveData<MonthModel> selectedMonth = new MutableLiveData<>();

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactionManager = new TransactionManager(application);
    }

    public void setUserId(int userId) {
        currentUserId.setValue(userId);
    }

    public void selectMonth(MonthModel monthModel) {
        selectedMonth.setValue(monthModel);
    }

    // 2. ĐỔI KIỂU TRẢ VỀ THÀNH LiveData<List<TransactionDTO>>
    public final LiveData<List<TransactionDTO>> rawTransactions = Transformations.switchMap(
            selectedMonth,
            month -> {
                Integer userId = currentUserId.getValue();
                if (userId == null || month == null) {
                    return new MutableLiveData<>(new ArrayList<>());
                }

                // Tính toán khoảng thời gian từ đầu tháng đến cuối tháng
                Calendar cal = Calendar.getInstance();

                // Đầu tháng: 00:00:00.000
                cal.set(month.getYear(), month.getMonth(), 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long start = cal.getTimeInMillis();

                // Cuối tháng: 23:59:59.999
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                long end = cal.getTimeInMillis();

                // Trả về LiveData chứa danh sách DTO từ Manager
                return transactionManager.getTransactionsByRange(userId, start, end);
            }
    );

    public List<TransactionGroup> processAndGroupTransactions(List<TransactionDTO> dtoList, String currentTab) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Lọc danh sách theo Tab
        List<TransactionDTO> filteredList = new ArrayList<>();

        for (TransactionDTO dto : dtoList) {
            if (dto == null || dto.getTransaction() == null) continue;

            Transaction t = dto.getTransaction();

            // Kiểm tra xem transaction có phải là Chi tiêu hay không
            // (Chi tiêu: type bằng EXPENSE hoặc amount < 0)
            boolean isExpense = "EXPENSE".equalsIgnoreCase(t.getType()) || t.getAmount() < 0;

            if ("EXPENSE".equalsIgnoreCase(currentTab)) {
                // Tab Chi tiêu: CHỈ LẤY các khoản Chi tiêu
                if (isExpense) {
                    filteredList.add(dto);
                }
            } else if ("INCOME".equalsIgnoreCase(currentTab)) {
                // Tab Thu nhập: CHỈ LẤY các khoản Thu nhập (KHÔNG PHẢI Chi tiêu)
                if (!isExpense) {
                    filteredList.add(dto);
                }
            } else {
                // Tab Tất cả: Lấy toàn bộ
                filteredList.add(dto);
            }
        }

        // 2. Gom nhóm theo Ngày
        Map<String, List<TransactionDTO>> groupedMap = new LinkedHashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));

        for (TransactionDTO dto : filteredList) {
            String dateKey = dateFormat.format(new Date(dto.getTransaction().getTransactionDate()));

            if (!groupedMap.containsKey(dateKey)) {
                groupedMap.put(dateKey, new ArrayList<>());
            }
            groupedMap.get(dateKey).add(dto);
        }

        // 3. Tính tổng tiền từng ngày (Dựa trên danh sách đã lọc)
        List<TransactionGroup> groupList = new ArrayList<>();
        for (Map.Entry<String, List<TransactionDTO>> entry : groupedMap.entrySet()) {
            String dateHeader = entry.getKey();
            List<TransactionDTO> dayTransactions = entry.getValue();

            long totalAmountOfDay = 0;
            for (TransactionDTO dto : dayTransactions) {
                Transaction t = dto.getTransaction();
                long absAmount = Math.abs(t.getAmount());
                boolean isExpense = "EXPENSE".equalsIgnoreCase(t.getType()) || t.getAmount() < 0;

                if (isExpense) {
                    totalAmountOfDay -= absAmount;
                } else {
                    totalAmountOfDay += absAmount;
                }
            }

            groupList.add(new TransactionGroup(dateHeader, totalAmountOfDay, dayTransactions));
        }

        return groupList;
    }
}