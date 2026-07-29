package com.example.myapplication.ui.analysis;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.analysis.CategoryDetailActivity;
import com.example.myapplication.adapter.AnalysisCategoryAdapter;
import com.example.myapplication.data.dto.AnalysisCategoryDTO;
import com.example.myapplication.data.dto.TransactionDTO;
import com.example.myapplication.data.entity.User;
import com.example.myapplication.manager.TransactionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalysisFragment extends Fragment {

    private User currentUser;
    private TransactionManager transactionManager;

    private TextView tabWeek, tabMonth, tabYear, tabCustom;
    private ImageView btnPrevTime, btnNextTime;
    private TextView tvSelectedTime;

    private TextView tvOverviewBalance, tvOverviewIncome, tvOverviewExpense;
    private TextView tabChartExpense, tabChartIncome, tabChartAll;

    private LineChart lineChart;
    private BarChart barChart;
    private PieChart pieChart;
    private TextView tvPieTitle;
    private RecyclerView rvRanking;
    private AnalysisCategoryAdapter adapter;

    private LiveData<List<TransactionDTO>> currentTransactionsLiveData;

    private String currentTimeMode = "MONTH"; // WEEK, MONTH, YEAR, CUSTOM
    private String currentChartType = "ALL"; // ALL, EXPENSE, INCOME

    private Calendar selectedCalendar;
    private long customStartDate = 0;
    private long customEndDate = 0;

    private DecimalFormat currencyFormat = new DecimalFormat("#,### đ");

    public AnalysisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("EXTRA_USER");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transactionManager = new TransactionManager(requireContext());
        selectedCalendar = Calendar.getInstance();

        initViews(view);
        setupListeners();
        setupRecyclerView();
        setupCharts();

        // Initial load
        updateTimeSelectorUI();
        switchChartType("ALL");
    }

    private void initViews(View v) {
        tabWeek = v.findViewById(R.id.tab_week);
        tabMonth = v.findViewById(R.id.tab_month);
        tabYear = v.findViewById(R.id.tab_year);
        tabCustom = v.findViewById(R.id.tab_custom);

        btnPrevTime = v.findViewById(R.id.btn_prev_time);
        btnNextTime = v.findViewById(R.id.btn_next_time);
        tvSelectedTime = v.findViewById(R.id.tv_selected_time);

        tvOverviewBalance = v.findViewById(R.id.tv_overview_balance);
        tvOverviewIncome = v.findViewById(R.id.tv_overview_income);
        tvOverviewExpense = v.findViewById(R.id.tv_overview_expense);

        tabChartExpense = v.findViewById(R.id.tab_chart_expense);
        tabChartIncome = v.findViewById(R.id.tab_chart_income);
        tabChartAll = v.findViewById(R.id.tab_chart_all);

        lineChart = v.findViewById(R.id.line_chart);
        barChart = v.findViewById(R.id.bar_chart);
        pieChart = v.findViewById(R.id.pie_chart);
        tvPieTitle = v.findViewById(R.id.tv_pie_title);
        rvRanking = v.findViewById(R.id.rv_ranking);
    }

    private void setupListeners() {
        // Time Modes
        tabWeek.setOnClickListener(v -> switchTimeMode("WEEK"));
        tabMonth.setOnClickListener(v -> switchTimeMode("MONTH"));
        tabYear.setOnClickListener(v -> switchTimeMode("YEAR"));
        tabCustom.setOnClickListener(v -> switchTimeMode("CUSTOM"));

        // Time Navigation
        btnPrevTime.setOnClickListener(v -> navigateTime(-1));
        btnNextTime.setOnClickListener(v -> navigateTime(1));
        tvSelectedTime.setOnClickListener(v -> {
            if ("CUSTOM".equals(currentTimeMode)) {
                pickCustomDateRange();
            }
        });

        tabChartExpense.setOnClickListener(v -> switchChartType("EXPENSE"));
        tabChartIncome.setOnClickListener(v -> switchChartType("INCOME"));
        tabChartAll.setOnClickListener(v -> switchChartType("ALL"));
    }

    private void setupRecyclerView() {
        adapter = new AnalysisCategoryAdapter(requireContext(), new ArrayList<>(), item -> {
            if (currentUser == null) return;
            long[] dates = getStartAndEndDates();
            Intent intent = new Intent(requireContext(), CategoryDetailActivity.class);
            intent.putExtra("categoryId", item.getCategoryId());
            intent.putExtra("categoryName", item.getCategoryName());
            intent.putExtra("categoryColor", item.getCategoryColor());
            intent.putExtra("userId", currentUser.getUserId());
            intent.putExtra("startDate", dates[0]);
            intent.putExtra("endDate", dates[1]);
            intent.putExtra("timeMode", currentTimeMode);
            intent.putExtra("chartType", currentChartType);
            startActivity(intent);
        });
        rvRanking.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRanking.setAdapter(adapter);
    }

    private void setupCharts() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setAxisMinimum(0f);

        // Setup BarChart
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setAxisMinimum(0f);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setDrawCenterText(false);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.getLegend().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);

        CustomMarkerView mv = new CustomMarkerView(requireContext(), R.layout.layout_marker_view);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);

        CustomMarkerView barMv = new CustomMarkerView(requireContext(), R.layout.layout_marker_view);
        barMv.setChartView(barChart);
        barChart.setMarker(barMv);

        CustomMarkerView pieMv = new CustomMarkerView(requireContext(), R.layout.layout_marker_view);
        pieMv.setChartView(pieChart);
        pieChart.setMarker(pieMv);
    }

    private void switchTimeMode(String mode) {
        currentTimeMode = mode;

        // Reset styles
        tabWeek.setBackgroundResource(android.R.color.transparent);
        tabWeek.setTextColor(0xFF757575);
        tabMonth.setBackgroundResource(android.R.color.transparent);
        tabMonth.setTextColor(0xFF757575);
        tabYear.setBackgroundResource(android.R.color.transparent);
        tabYear.setTextColor(0xFF757575);
        tabCustom.setBackgroundResource(android.R.color.transparent);
        tabCustom.setTextColor(0xFF757575);

        TextView activeTab = null;
        switch (mode) {
            case "WEEK": activeTab = tabWeek; break;
            case "MONTH": activeTab = tabMonth; break;
            case "YEAR": activeTab = tabYear; break;
            case "CUSTOM": activeTab = tabCustom; break;
        }

        if (activeTab != null) {
            activeTab.setBackgroundResource(R.drawable.bg_toggle_active);
            activeTab.setTextColor(0xFFFFFFFF);
            if (activeTab != tabMonth) activeTab.setTypeface(null, android.graphics.Typeface.NORMAL); // Reset to default
            activeTab.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        if (mode.equals("CUSTOM")) {
            btnPrevTime.setVisibility(View.INVISIBLE);
            btnNextTime.setVisibility(View.INVISIBLE);
            if (customStartDate == 0) {
                pickCustomDateRange();
            } else {
                updateTimeSelectorUI();
                loadData();
            }
        } else {
            btnPrevTime.setVisibility(View.VISIBLE);
            btnNextTime.setVisibility(View.VISIBLE);
            updateTimeSelectorUI();
            loadData();
        }
    }

    private void switchChartType(String type) {
        currentChartType = type;

        tabChartExpense.setBackgroundResource(android.R.color.transparent);
        tabChartExpense.setTextColor(0xFF757575);
        tabChartIncome.setBackgroundResource(android.R.color.transparent);
        tabChartIncome.setTextColor(0xFF757575);
        tabChartAll.setBackgroundResource(android.R.color.transparent);
        tabChartAll.setTextColor(0xFF757575);

        TextView activeTab = null;
        switch (type) {
            case "EXPENSE": activeTab = tabChartExpense; break;
            case "INCOME": activeTab = tabChartIncome; break;
            case "ALL": activeTab = tabChartAll; break;
        }

        if (activeTab != null) {
            activeTab.setBackgroundResource(R.drawable.bg_toggle_active);
            activeTab.setTextColor(0xFFFFFFFF);
            activeTab.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        if ("ALL".equals(currentChartType)) {
            lineChart.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
            rvRanking.setVisibility(View.GONE);
            tvPieTitle.setText("Cán cân Thu - Chi");
        } else {
            lineChart.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            rvRanking.setVisibility(View.VISIBLE);
            tvPieTitle.setText("Tỷ trọng danh mục");
        }

        loadData(); // Reload data with new filter
    }

    private void navigateTime(int offset) {
        if ("WEEK".equals(currentTimeMode)) {
            selectedCalendar.add(Calendar.WEEK_OF_YEAR, offset);
        } else if ("MONTH".equals(currentTimeMode)) {
            selectedCalendar.add(Calendar.MONTH, offset);
        } else if ("YEAR".equals(currentTimeMode)) {
            selectedCalendar.add(Calendar.YEAR, offset);
        }
        updateTimeSelectorUI();
        loadData();
    }

    private void pickCustomDateRange() {
        Calendar temp = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            Calendar startCal = Calendar.getInstance();
            startCal.set(year, month, dayOfMonth, 0, 0, 0);
            customStartDate = startCal.getTimeInMillis();

            new DatePickerDialog(requireContext(), (view2, year2, month2, dayOfMonth2) -> {
                Calendar endCal = Calendar.getInstance();
                endCal.set(year2, month2, dayOfMonth2, 23, 59, 59);
                customEndDate = endCal.getTimeInMillis();

                if (customEndDate < customStartDate) {
                    long tempTime = customStartDate;
                    customStartDate = customEndDate;
                    customEndDate = tempTime;
                }

                updateTimeSelectorUI();
                loadData();
            }, year, month, dayOfMonth).show();

        }, temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTimeSelectorUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if ("WEEK".equals(currentTimeMode)) {
            Calendar c = (Calendar) selectedCalendar.clone();
            c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
            String startStr = sdf.format(c.getTime());
            c.add(Calendar.DAY_OF_WEEK, 6);
            String endStr = sdf.format(c.getTime());
            tvSelectedTime.setText(startStr + " - " + endStr);
        } else if ("MONTH".equals(currentTimeMode)) {
            SimpleDateFormat sdfMonth = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
            tvSelectedTime.setText("Tháng " + sdfMonth.format(selectedCalendar.getTime()));
        } else if ("YEAR".equals(currentTimeMode)) {
            tvSelectedTime.setText("Năm " + selectedCalendar.get(Calendar.YEAR));
        } else if ("CUSTOM".equals(currentTimeMode)) {
            if (customStartDate != 0 && customEndDate != 0) {
                tvSelectedTime.setText(sdf.format(new Date(customStartDate)) + " - " + sdf.format(new Date(customEndDate)));
            } else {
                tvSelectedTime.setText("Chọn ngày");
            }
        }
    }

    private long[] getStartAndEndDates() {
        long[] dates = new long[2]; // 0: start, 1: end
        Calendar c = (Calendar) selectedCalendar.clone();

        if ("WEEK".equals(currentTimeMode)) {
            c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
            c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0);
            dates[0] = c.getTimeInMillis();
            c.add(Calendar.DAY_OF_WEEK, 6);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
            dates[1] = c.getTimeInMillis();
        } else if ("MONTH".equals(currentTimeMode)) {
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0);
            dates[0] = c.getTimeInMillis();
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
            dates[1] = c.getTimeInMillis();
        } else if ("YEAR".equals(currentTimeMode)) {
            c.set(Calendar.DAY_OF_YEAR, 1);
            c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0);
            dates[0] = c.getTimeInMillis();
            c.set(Calendar.MONTH, 11); c.set(Calendar.DAY_OF_MONTH, 31);
            c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59); c.set(Calendar.SECOND, 59);
            dates[1] = c.getTimeInMillis();
        } else if ("CUSTOM".equals(currentTimeMode)) {
            dates[0] = customStartDate;
            dates[1] = customEndDate;
        }
        return dates;
    }

    private void loadData() {
        if (currentUser == null) return;

        long[] dates = getStartAndEndDates();
        if (dates[0] == 0 || dates[1] == 0) return; // CUSTOM not selected

        if (currentTransactionsLiveData != null) {
            currentTransactionsLiveData.removeObservers(getViewLifecycleOwner());
        }

        currentTransactionsLiveData = transactionManager.getTransactionsByRange(currentUser.getUserId(), dates[0], dates[1]);
        currentTransactionsLiveData.observe(getViewLifecycleOwner(), this::processData);
    }

    private void processData(List<TransactionDTO> rawList) {
        if (rawList == null) rawList = new ArrayList<>();

        long totalIncome = 0;
        long totalExpense = 0;

        List<TransactionDTO> filteredList = new ArrayList<>();

        for (TransactionDTO dto : rawList) {
            String type = dto.getTransaction().getType();
            long amt = Math.abs(dto.getTransaction().getAmount());

            boolean isExpense = "EXPENSE".equalsIgnoreCase(type) || dto.getTransaction().getAmount() < 0;

            if (!isExpense) {
                totalIncome += amt;
            } else {
                totalExpense += amt;
            }

            // Filter by chart tab
            if ("ALL".equals(currentChartType)) {
                filteredList.add(dto);
            } else if ("EXPENSE".equals(currentChartType) && isExpense) {
                filteredList.add(dto);
            } else if ("INCOME".equals(currentChartType) && !isExpense) {
                filteredList.add(dto);
            }
        }

        long balance = totalIncome - totalExpense;

        // Update Overview Card (Thu is GREEN, Chi is RED as per user's final clarification)
        tvOverviewIncome.setText("+" + currencyFormat.format(totalIncome));
        tvOverviewIncome.setTextColor(0xFF4CAF50); // Green
        tvOverviewExpense.setText("-" + currencyFormat.format(totalExpense));
        tvOverviewExpense.setTextColor(0xFFF44336); // Red
        tvOverviewBalance.setText(currencyFormat.format(balance));
        if (balance < 0) {
            tvOverviewBalance.setTextColor(0xFFF44336);
        } else {
            tvOverviewBalance.setTextColor(0xFF212121);
        }

        // Process for Charts
        if ("ALL".equals(currentChartType)) {
            processBarChart(rawList);
        } else {
            processLineChart(filteredList);
        }

        processCategoryData(rawList, filteredList, totalIncome, totalExpense);
    }

    private void processLineChart(List<TransactionDTO> list) {
        if (list.isEmpty()) {
            lineChart.clear();
            return;
        }

        // Group by start of day (timestamp) to make X axis proportional
        Map<Long, Long> dayAmountMap = new HashMap<>();

        for (TransactionDTO dto : list) {
            long time = dto.getTransaction().getTransactionDate();

            // Normalize to start of day
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long dayStart = cal.getTimeInMillis();

            long amt = Math.abs(dto.getTransaction().getAmount());
            dayAmountMap.put(dayStart, dayAmountMap.getOrDefault(dayStart, 0L) + amt);
        }

        // Sort keys
        List<Long> sortedDays = new ArrayList<>(dayAmountMap.keySet());
        Collections.sort(sortedDays);

        long referenceDay = sortedDays.get(0);
        long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

        List<Entry> entries = new ArrayList<>();
        for (Long day : sortedDays) {
            float x = (day - referenceDay) / (float) DAY_IN_MILLIS;
            entries.add(new Entry(x, dayAmountMap.get(day)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Biến động");

        // Use color based on type
        int color = 0xFF2196F3; // Blue for ALL
        if ("EXPENSE".equals(currentChartType)) color = 0xFFF44336; // Red
        if ("INCOME".equals(currentChartType)) color = 0xFF4CAF50; // Green

        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawValues(false); // Ẩn số trên line để không bị đè nhau
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(color);
        dataSet.setFillAlpha(30);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Format X Axis to show proper dates proportionally and avoid duplicates
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
            @Override
            public String getFormattedValue(float value) {
                long time = referenceDay + (long) (value * DAY_IN_MILLIS);
                return sdf.format(new Date(time));
            }
        });

        // Auto zoom and scroll to the latest data if there are many points
        lineChart.setVisibleXRangeMaximum(14f); // Show max 14 days at a time
        if (!entries.isEmpty()) {
            lineChart.moveViewToX(entries.get(entries.size() - 1).getX());
        }

        lineChart.invalidate();
    }

    private void processBarChart(List<TransactionDTO> list) {
        if (list.isEmpty()) {
            barChart.clear();
            return;
        }

        long DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;
        long[] dates = getStartAndEndDates();
        long durationMillis = dates[1] - dates[0];

        boolean groupByMonth = false;
        if ("YEAR".equals(currentTimeMode)) {
            groupByMonth = true;
        } else if ("CUSTOM".equals(currentTimeMode) && durationMillis > 32 * DAY_IN_MILLIS) {
            groupByMonth = true;
        }

        Map<Long, long[]> amountMap = new HashMap<>(); // [0] = Income, [1] = Expense

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
            boolean isExpense = "EXPENSE".equalsIgnoreCase(dto.getTransaction().getType()) || dto.getTransaction().getAmount() < 0;

            long[] amounts = amountMap.getOrDefault(key, new long[]{0L, 0L});
            if (isExpense) {
                amounts[1] += amt;
            } else {
                amounts[0] += amt;
            }
            amountMap.put(key, amounts);
        }

        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();

        int count = 0;
        Calendar iterCal = Calendar.getInstance();
        iterCal.setTimeInMillis(dates[0]); // startDate
        iterCal.set(Calendar.HOUR_OF_DAY, 0);
        iterCal.set(Calendar.MINUTE, 0);
        iterCal.set(Calendar.SECOND, 0);
        iterCal.set(Calendar.MILLISECOND, 0);

        if ("YEAR".equals(currentTimeMode)) {
            iterCal.set(Calendar.DAY_OF_MONTH, 1);
            iterCal.set(Calendar.MONTH, Calendar.JANUARY);
            for (int i = 0; i < 12; i++) {
                long currentKey = iterCal.getTimeInMillis();
                long[] amounts = amountMap.getOrDefault(currentKey, new long[]{0L, 0L});
                incomeEntries.add(new BarEntry(count, amounts[0]));
                expenseEntries.add(new BarEntry(count, amounts[1]));
                count++;
                iterCal.add(Calendar.MONTH, 1);
            }
        } else if ("WEEK".equals(currentTimeMode)) {
            for (int i = 0; i < 7; i++) {
                long currentKey = iterCal.getTimeInMillis();
                long[] amounts = amountMap.getOrDefault(currentKey, new long[]{0L, 0L});
                incomeEntries.add(new BarEntry(count, amounts[0]));
                expenseEntries.add(new BarEntry(count, amounts[1]));
                count++;
                iterCal.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else if ("MONTH".equals(currentTimeMode)) {
            int maxDays = iterCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 0; i < maxDays; i++) {
                long currentKey = iterCal.getTimeInMillis();
                long[] amounts = amountMap.getOrDefault(currentKey, new long[]{0L, 0L});
                incomeEntries.add(new BarEntry(count, amounts[0]));
                expenseEntries.add(new BarEntry(count, amounts[1]));
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
                long[] amounts = amountMap.getOrDefault(currentKey, new long[]{0L, 0L});
                incomeEntries.add(new BarEntry(count, amounts[0]));
                expenseEntries.add(new BarEntry(count, amounts[1]));
                count++;

                if (groupByMonth) {
                    iterCal.add(Calendar.MONTH, 1);
                } else {
                    iterCal.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        }

        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Thu nhập");
        incomeSet.setColor(0xFF4CAF50); // Green
        incomeSet.setDrawValues(false);

        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Chi tiêu");
        expenseSet.setColor(0xFFF44336); // Red
        expenseSet.setDrawValues(false);

        BarData data = new BarData(incomeSet, expenseSet);

        float groupSpace = 0.06f;
        float barSpace = 0.02f;
        float barWidth = 0.45f;
        data.setBarWidth(barWidth);

        barChart.setData(data);
        barChart.getXAxis().setAxisMinimum(0f);
        barChart.getXAxis().setAxisMaximum(count);
        barChart.groupBars(0f, groupSpace, barSpace);
        barChart.getXAxis().setCenterAxisLabels(true);
        barChart.getXAxis().setGranularity(1f);

        boolean finalGroupByMonth = groupByMonth;
        final int finalCount = count;
        final long finalStartDate = dates[0];

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat sdfDay = new SimpleDateFormat("dd/MM", Locale.getDefault());
            private final SimpleDateFormat sdfMonth = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
            private final SimpleDateFormat sdfDayOfWeek = new SimpleDateFormat("EEE", new Locale("vi", "VN"));

            @Override
            public String getFormattedValue(float value) {
                if (value < 0 || value >= finalCount) return "";
                Calendar c = Calendar.getInstance();

                if ("YEAR".equals(currentTimeMode)) {
                    c.setTimeInMillis(finalStartDate);
                    c.set(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.add(Calendar.MONTH, (int) value);
                    return "T" + (c.get(Calendar.MONTH) + 1);
                } else if ("WEEK".equals(currentTimeMode)) {
                    c.setTimeInMillis(finalStartDate);
                    c.add(Calendar.DAY_OF_MONTH, (int) value);
                    return sdfDayOfWeek.format(c.getTime());
                } else if ("MONTH".equals(currentTimeMode)) {
                    c.setTimeInMillis(finalStartDate);
                    c.add(Calendar.DAY_OF_MONTH, (int) value);
                    return String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                } else {
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

        barChart.setVisibleXRangeMaximum(7f);
        if (count > 0) {
            barChart.moveViewToX(count - 1);
        }

        barChart.invalidate();
    }

    private void processCategoryData(List<TransactionDTO> rawList, List<TransactionDTO> filteredList, long totalIncome, long totalExpense) {
        if ("ALL".equals(currentChartType)) {
            rvRanking.setVisibility(View.GONE);
            if (totalIncome == 0 && totalExpense == 0) {
                pieChart.clear();
                return;
            }

            List<PieEntry> entries = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();

            long total = totalIncome + totalExpense;

            if (totalIncome > 0) {
                AnalysisCategoryDTO incDto = new AnalysisCategoryDTO(0, "Thu nhập", "", "", totalIncome, (float) totalIncome * 100 / total);
                entries.add(new PieEntry(incDto.getPercentage(), "Thu nhập", incDto));
                colors.add(0xFF4CAF50); // Green
            }
            if (totalExpense > 0) {
                AnalysisCategoryDTO expDto = new AnalysisCategoryDTO(0, "Chi tiêu", "", "", totalExpense, (float) totalExpense * 100 / total);
                entries.add(new PieEntry(expDto.getPercentage(), "Chi tiêu", expDto));
                colors.add(0xFFF44336); // Red
            }

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(colors);
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(0f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getPieLabel(float value, PieEntry pieEntry) {
                    if (value < 5f) return "";
                    return String.format(Locale.getDefault(), "%.1f%%", value);
                }
            });
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            pieChart.setHighlightPerTapEnabled(true);
            pieChart.setData(data);
            pieChart.invalidate();
            return;
        }

        rvRanking.setVisibility(View.VISIBLE);
        long totalTypeAmount = currentChartType.equals("EXPENSE") ? totalExpense : totalIncome;

        if (filteredList.isEmpty() || totalTypeAmount == 0) {
            pieChart.clear();
            adapter.setList(new ArrayList<>());
            return;
        }

        Map<Integer, AnalysisCategoryDTO> map = new HashMap<>();
        for (TransactionDTO dto : filteredList) {
            int catId = dto.getTransaction().getCategoryId();
            if (!map.containsKey(catId)) {
                map.put(catId, new AnalysisCategoryDTO(
                        catId,
                        dto.getCategoryName(),
                        dto.getIconName(),
                        dto.getCategoryColor(),
                        0, 0f
                ));
            }
            AnalysisCategoryDTO catDTO = map.get(catId);
            catDTO.setTotalAmount(catDTO.getTotalAmount() + Math.abs(dto.getTransaction().getAmount()));
        }

        List<AnalysisCategoryDTO> resultList = new ArrayList<>(map.values());
        for (AnalysisCategoryDTO dto : resultList) {
            dto.setPercentage((float) dto.getTotalAmount() / totalTypeAmount * 100f);
        }

        // Sort by amount DESC
        Collections.sort(resultList, (o1, o2) -> Long.compare(o2.getTotalAmount(), o1.getTotalAmount()));

        adapter.setList(resultList);

        // Build PieChart
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (AnalysisCategoryDTO dto : resultList) {
            String labelName = dto.getPercentage() < 10f ? "" : dto.getCategoryName();
            entries.add(new PieEntry(dto.getPercentage(), labelName, dto));
            try {
                colors.add(Color.parseColor(dto.getCategoryColor()));
            } catch (Exception e) {
                colors.add(Color.LTGRAY);
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Danh mục");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(0f); // Tắt hiệu ứng nẩy lên (highlight) gây bối rối

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                if (value < 5f) return ""; // Ẩn chữ nếu bé hơn 5%
                return String.format(Locale.getDefault(), "%.1f%%", value);
            }
        });
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setHighlightPerTapEnabled(true); // Bật lại tính năng chạm để xem tooltip
        pieChart.setData(data);
        pieChart.invalidate();
    }
}