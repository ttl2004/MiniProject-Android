package com.example.myapplication.activity.analysis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.TransactionDetailAdapter;
import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.manager.TransactionManager;
import com.example.myapplication.ui.analysis.CustomMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CategoryDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvCategoryName, tvTimeRange, tvEmpty;
    private BarChart barChart;
    private RecyclerView rvTransactions;

    private TransactionManager transactionManager;
    private TransactionDetailAdapter adapter;

    private int categoryId;
    private String categoryName;
    private String categoryColor;
    private int userId;
    private long startDate;
    private long endDate;
    private String timeMode;
    private String chartType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        transactionManager = new TransactionManager(this);

        // Get Intent data
        Intent intent = getIntent();
        categoryId = intent.getIntExtra("categoryId", -1);
        categoryName = intent.getStringExtra("categoryName");
        categoryColor = intent.getStringExtra("categoryColor");
        userId = intent.getIntExtra("userId", -1);
        startDate = intent.getLongExtra("startDate", 0);
        endDate = intent.getLongExtra("endDate", 0);
        timeMode = intent.getStringExtra("timeMode");
        chartType = intent.getStringExtra("chartType");

        initViews();
        setupListeners();
        setupChart();
        setupRecyclerView();
        
        updateHeaderUI();
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvCategoryName = findViewById(R.id.tv_category_name);
        tvTimeRange = findViewById(R.id.tv_time_range);
        tvEmpty = findViewById(R.id.tv_empty);
        barChart = findViewById(R.id.bar_chart);
        rvTransactions = findViewById(R.id.rv_transactions);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleXEnabled(true);
        barChart.setScaleYEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setAutoScaleMinMaxEnabled(true);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setSpaceTop(30f);
        
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.layout_marker_view);
        mv.setChartView(barChart);
        barChart.setMarker(mv);
    }

    private void setupRecyclerView() {
        // Pass null for listener so Edit/Delete buttons are hidden
        adapter = new TransactionDetailAdapter(new ArrayList<>(), null);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);
    }

    private void updateHeaderUI() {
        String catName = categoryName != null ? categoryName : "Danh mục";
        
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        
        String timeStr = "";
        if ("WEEK".equals(timeMode)) {
            timeStr = sdfDay.format(new Date(startDate)) + " - " + sdfDay.format(new Date(endDate));
        } else if ("MONTH".equals(timeMode)) {
            timeStr = "Tháng " + sdfMonth.format(new Date(startDate));
        } else if ("YEAR".equals(timeMode)) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(startDate);
            timeStr = "Năm " + c.get(Calendar.YEAR);
        } else {
            timeStr = sdfDay.format(new Date(startDate)) + " - " + sdfDay.format(new Date(endDate));
        }

        tvCategoryName.setText(catName + " - " + timeStr);
        tvTimeRange.setVisibility(View.GONE);
    }

    private void loadData() {
        if (userId == -1 || startDate == 0 || endDate == 0) return;

        transactionManager.getTransactionsByRange(userId, startDate, endDate)
                .observe(this, this::processData);
    }

    private void processData(List<TransactionDTO> rawList) {
        if (rawList == null) rawList = new ArrayList<>();
        
        List<TransactionDTO> filteredList = new ArrayList<>();
        for (TransactionDTO dto : rawList) {
            boolean isExpense = "EXPENSE".equalsIgnoreCase(dto.getTransaction().getType()) || dto.getTransaction().getAmount() < 0;
            boolean matchType = true;
            if ("EXPENSE".equals(chartType) && !isExpense) matchType = false;
            if ("INCOME".equals(chartType) && isExpense) matchType = false;

            if (dto.getTransaction().getCategoryId() == categoryId && matchType) {
                filteredList.add(dto);
            }
        }
        
        // Cập nhật danh sách
        adapter.setDtoList(filteredList);
        if (filteredList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
            barChart.clear();
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
            processChartData(filteredList);
        }
    }

    private void processChartData(List<TransactionDTO> list) {
        long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;
        long durationMillis = endDate - startDate;
        
        boolean groupByMonth = false;
        
        if ("YEAR".equals(timeMode)) {
            groupByMonth = true;
        } else if ("CUSTOM".equals(timeMode)) {
            if (durationMillis > 32 * DAY_IN_MILLIS) {
                groupByMonth = true;
            }
        }

        Map<Long, Long> amountMap = new HashMap<>();

        for (TransactionDTO dto : list) {
            long time = dto.getTransaction().getTransactionDate();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            
            if (groupByMonth) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
            }
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            
            long key = cal.getTimeInMillis();
            long amt = Math.abs(dto.getTransaction().getAmount());
            amountMap.put(key, amountMap.getOrDefault(key, 0L) + amt);
        }

        List<BarEntry> entries = new ArrayList<>();
        int count = 0;
        
        Calendar iterCal = Calendar.getInstance();
        iterCal.setTimeInMillis(startDate); // Dùng startDate để fill đủ số cột
        iterCal.set(Calendar.HOUR_OF_DAY, 0);
        iterCal.set(Calendar.MINUTE, 0);
        iterCal.set(Calendar.SECOND, 0);
        iterCal.set(Calendar.MILLISECOND, 0);
        
        if ("YEAR".equals(timeMode)) {
            // Hiện đúng 12 tháng của năm
            iterCal.set(Calendar.DAY_OF_MONTH, 1);
            iterCal.set(Calendar.MONTH, Calendar.JANUARY);
            for (int i = 0; i < 12; i++) {
                long currentKey = iterCal.getTimeInMillis();
                long amount = amountMap.getOrDefault(currentKey, 0L);
                entries.add(new BarEntry(count, amount));
                count++;
                iterCal.add(Calendar.MONTH, 1);
            }
        } else if ("WEEK".equals(timeMode)) {
            // Hiện đúng 7 ngày của tuần
            for (int i = 0; i < 7; i++) {
                long currentKey = iterCal.getTimeInMillis();
                long amount = amountMap.getOrDefault(currentKey, 0L);
                entries.add(new BarEntry(count, amount));
                count++;
                iterCal.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else if ("MONTH".equals(timeMode)) {
            // Hiện đúng số ngày trong tháng
            int maxDays = iterCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 0; i < maxDays; i++) {
                long currentKey = iterCal.getTimeInMillis();
                long amount = amountMap.getOrDefault(currentKey, 0L);
                entries.add(new BarEntry(count, amount));
                count++;
                iterCal.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            // CUSTOM mode
            List<Long> sortedKeys = new ArrayList<>(amountMap.keySet());
            if (sortedKeys.isEmpty()) {
                barChart.clear();
                return;
            }
            long minTime = sortedKeys.get(0);
            long maxTime = sortedKeys.get(sortedKeys.size() - 1);
            iterCal.setTimeInMillis(minTime);
            
            while (iterCal.getTimeInMillis() <= maxTime) {
                long currentKey = iterCal.getTimeInMillis();
                long amount = amountMap.getOrDefault(currentKey, 0L);
                entries.add(new BarEntry(count, amount));
                count++;
                
                if (groupByMonth) {
                    iterCal.add(Calendar.MONTH, 1);
                } else {
                    iterCal.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, categoryName);
        int color = Color.LTGRAY;
        try {
            if (categoryColor != null && !categoryColor.isEmpty()) {
                color = Color.parseColor(categoryColor);
            }
        } catch (Exception ignored) {}
        
        dataSet.setColor(color);
        dataSet.setDrawValues(false);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);
        
        barChart.setData(data);
        barChart.getXAxis().setAxisMinimum(-0.5f);
        barChart.getXAxis().setAxisMaximum(count - 0.5f);
        barChart.getXAxis().setGranularity(1f);
        
        boolean finalGroupByMonth = groupByMonth;
        final int finalCount = count;
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat sdfDay = new SimpleDateFormat("dd/MM", Locale.getDefault());
            private final SimpleDateFormat sdfMonth = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
            private final SimpleDateFormat sdfDayOfWeek = new SimpleDateFormat("EEE", new Locale("vi", "VN"));
            
            @Override
            public String getFormattedValue(float value) {
                if (value < 0 || value >= finalCount) return "";
                Calendar c = Calendar.getInstance();
                
                if ("YEAR".equals(timeMode)) {
                    c.setTimeInMillis(startDate);
                    c.set(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.add(Calendar.MONTH, (int) value);
                    return "T" + (c.get(Calendar.MONTH) + 1);
                } else if ("WEEK".equals(timeMode)) {
                    c.setTimeInMillis(startDate);
                    c.add(Calendar.DAY_OF_MONTH, (int) value);
                    return sdfDayOfWeek.format(c.getTime());
                } else if ("MONTH".equals(timeMode)) {
                    c.setTimeInMillis(startDate);
                    c.add(Calendar.DAY_OF_MONTH, (int) value);
                    return String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                } else {
                    // CUSTOM
                    List<Long> sortedKeys = new ArrayList<>(amountMap.keySet());
                    if (sortedKeys.isEmpty()) return "";
                    long minTime = sortedKeys.get(0);
                    c.setTimeInMillis(minTime);
                    
                    if (finalGroupByMonth) {
                        c.add(Calendar.MONTH, (int) value);
                        return sdfMonth.format(c.getTime());
                    } else {
                        c.add(Calendar.DAY_OF_MONTH, (int) value);
                        return sdfDay.format(c.getTime());
                    }
                }
            }
        });
        
        barChart.setVisibleXRangeMaximum(12f);
        if (count > 0) {
            barChart.moveViewToX(count - 1);
        }
        
        barChart.invalidate();
    }
}
