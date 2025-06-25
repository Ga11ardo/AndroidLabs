package com.labs.lab6;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatisticsActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private BarChart barChart;
    private List<Task> completedTasks;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final int PERIOD_DAY = 0;
    private static final int PERIOD_MONTH = 1;
    private static final int PERIOD_YEAR = 2;
    private int currentPeriod = PERIOD_DAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar_stats);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Статистика виконаних завдань");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        barChart = findViewById(R.id.bar_chart);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        RadioGroup radioGroup = findViewById(R.id.rg_period);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_day) {
                currentPeriod = PERIOD_DAY;
            } else if (checkedId == R.id.rb_month) {
                currentPeriod = PERIOD_MONTH;
            } else if (checkedId == R.id.rb_year) {
                currentPeriod = PERIOD_YEAR;
            }
            updateChart();
        });

        // Load data in the background
        executor.execute(() -> {
            completedTasks = taskViewModel.getAllCompletedTasksForStats();
            // Update UI on the main thread
            runOnUiThread(this::updateChart);
        });
    }

    private void updateChart() {
        if (completedTasks == null || completedTasks.isEmpty()) {
            barChart.clear();
            barChart.setNoDataText("Немає даних для відображення");
            barChart.invalidate();
            return;
        }

        Map<String, Integer> dataMap = processDataForPeriod(currentPeriod);
        if (dataMap.isEmpty()) {
            barChart.clear();
            barChart.setNoDataText("Немає даних для вибраного періоду");
            barChart.invalidate();
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Кількість виконаних завдань");
        dataSet.setColor(getResources().getColor(R.color.purple_500));
        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        setupChartAppearance(labels);
        barChart.invalidate(); // refresh
    }

    private void setupChartAppearance(List<String> labels) {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
    }

    private Map<String, Integer> processDataForPeriod(int period) {
        Map<String, Integer> dataMap = new HashMap<>();
        String pattern;
        switch (period) {
            case PERIOD_MONTH:
                pattern = "MMM yyyy"; // e.g., Кві 2024
                break;
            case PERIOD_YEAR:
                pattern = "yyyy"; // e.g., 2024
                break;
            case PERIOD_DAY:
            default:
                pattern = "dd.MM.yy"; // e.g., 21.04.24
                break;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, new Locale("uk", "UA"));
        Calendar cal = Calendar.getInstance();

        for (Task task : completedTasks) {
            cal.setTimeInMillis(task.completionTimestamp);
            String key = sdf.format(cal.getTime());
            dataMap.put(key, dataMap.getOrDefault(key, 0) + 1);
        }
        return dataMap;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
