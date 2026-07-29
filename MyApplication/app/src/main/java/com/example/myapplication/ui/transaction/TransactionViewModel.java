package com.example.myapplication.ui.transaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.data.dto.TransactionDTO;
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
import java.util.Objects;
import java.util.concurrent.Executors;

public class TransactionViewModel extends AndroidViewModel {

    private final TransactionManager transactionManager;

    private final MutableLiveData<Integer> currentUserId = new MutableLiveData<>();
    private final MutableLiveData<MonthModel> selectedMonth = new MutableLiveData<>();

    // Class helper đệm dữ liệu đầu vào cho MediatorLiveData
    private static class FilterParams {
        @Nullable final Integer userId;
        @Nullable final MonthModel monthModel;

        FilterParams(@Nullable Integer userId, @Nullable MonthModel monthModel) {
            this.userId = userId;
            this.monthModel = monthModel;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FilterParams that = (FilterParams) o;
            return Objects.equals(userId, that.userId) && Objects.equals(monthModel, that.monthModel);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, monthModel);
        }
    }

    private final MediatorLiveData<FilterParams> filterParamsLiveData = new MediatorLiveData<>();
    public final LiveData<List<TransactionDTO>> rawTransactions;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactionManager = new TransactionManager(application);

        // 🟢 FIX BUG-16: Reactive với CẢ userId lẫn selectedMonth
        filterParamsLiveData.addSource(currentUserId, userId ->
                filterParamsLiveData.setValue(new FilterParams(userId, selectedMonth.getValue()))
        );

        filterParamsLiveData.addSource(selectedMonth, month ->
                filterParamsLiveData.setValue(new FilterParams(currentUserId.getValue(), month))
        );

        // SwitchMap lắng nghe sự thay đổi từ filterParamsLiveData
        rawTransactions = Transformations.switchMap(filterParamsLiveData, params -> {
            if (params.userId == null || params.monthModel == null) {
                return new MutableLiveData<>(new ArrayList<>());
            }

            MonthModel month = params.monthModel;
            Calendar cal = Calendar.getInstance();

            // Lưu ý: Nếu monthModel.getMonth() trả về từ 1-12 thì cần (month.getMonth() - 1)
            // Giả định getMonth() trong MonthModel trả về 0-11 chuẩn Calendar, hoặc 1-12
            int monthIndex = month.getMonth() > 11 ? month.getMonth() - 1 : month.getMonth();

            // Đầu tháng: 00:00:00.000
            cal.set(month.getYear(), monthIndex, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long start = cal.getTimeInMillis();

            // Cuối tháng: 23:59:59.999
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            long end = cal.getTimeInMillis();

            return transactionManager.getTransactionsByRange(params.userId, start, end);
        });
    }

    public void setUserId(int userId) {
        currentUserId.setValue(userId);
    }

    public void selectMonth(MonthModel monthModel) {
        selectedMonth.setValue(monthModel);
    }

    // Xóa giao dịch khỏi Database
    public void deleteTransaction(Transaction transaction) {
        if (transaction != null && currentUserId.getValue() != null
                && transaction.getUserId() == currentUserId.getValue()) {
            Executors.newSingleThreadExecutor().execute(() -> {
                transactionManager.delete(transaction);
            });
        }
    }

    public List<TransactionGroup> processAndGroupTransactions(List<TransactionDTO> dtoList, String currentTab) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Lọc danh sách theo Tab
        List<TransactionDTO> filteredList = new ArrayList<>();

        for (TransactionDTO dto : dtoList) {
            if (dto == null || dto.getTransaction() == null) continue;

            Transaction t = dto.getTransaction();
            boolean isExpense = "EXPENSE".equalsIgnoreCase(t.getType()) || t.getAmount() < 0;

            if ("EXPENSE".equalsIgnoreCase(currentTab)) {
                if (isExpense) filteredList.add(dto);
            } else if ("INCOME".equalsIgnoreCase(currentTab)) {
                if (!isExpense) filteredList.add(dto);
            } else {
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

        // 3. Tính tổng tiền từng ngày
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