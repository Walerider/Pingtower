package com.walerider.pingdom.fragments.charts;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.walerider.pingdom.R;
import com.walerider.pingdom.api.API;
import com.walerider.pingdom.api.APIClient;
import com.walerider.pingdom.api.entitys.PingtowerResponse;
import com.walerider.pingdom.utils.TokenStorage;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsChartFragment extends Fragment {

    private LineChart lineChart;
    private ProgressBar progressBar;
    private Long siteId;
    private ImageButton menuButton;

    public EventsChartFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            siteId = getArguments().getLong("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_chart, container, false);

        lineChart = view.findViewById(R.id.lineChartView);
        progressBar = view.findViewById(R.id.progress_bar);
        menuButton = view.findViewById(R.id.menuButtonLinear);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);

                popup.getMenuInflater().inflate(R.menu.graph_popup_menu, popup.getMenu());
                NavController navController = Navigation.findNavController(requireView());
                Bundle b = new Bundle();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.linear_graph) {
                            b.putLong("id", siteId);
                            navController.navigate(R.id.eventsChartFragment, b, null);
                            return true;
                        }

                        if (itemId == R.id.pie_graph) {
                            b.putLong("id", siteId);
                            navController.navigate(R.id.pieChartFragment,b,null);
                            return true;
                        }

                        if (itemId == R.id.bar_graph) {
                            b.putLong("id", siteId);
                            navController.navigate(R.id.barChartFragment, b, null);
                            return true;
                        }

                        return false;
                    }
                });

                popup.show();
            }
        });
        setupChart();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        calendar.add(Calendar.DAY_OF_MONTH, -3);
        Date threeDaysAgo = calendar.getTime();
        String dateFrom = String.format(locale, "%tF\n", threeDaysAgo);
        String dateTo = String.format(locale, "%tF\n", now);

        try {
            loadSiteStats(siteId, dateFrom, dateTo);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSiteStats(Long id, String dateFrom, String dateTo) throws NoSuchAlgorithmException, KeyManagementException {
        if (id == null) {
            Toast.makeText(requireContext(), "ID сайта не указан", Toast.LENGTH_SHORT).show();
            return;
        }

        API apiService = APIClient.getApi(requireContext());
        String token = TokenStorage.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Токен не найден. Пожалуйста, войдите снова.", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Call<PingtowerResponse> call = apiService.getSiteStatsByDate("Bearer " + token, id, dateFrom, dateTo);

        call.enqueue(new Callback<PingtowerResponse>() {
            @Override
            public void onResponse(Call<PingtowerResponse> call, Response<PingtowerResponse> response) {
                progressBar.setVisibility(View.GONE);
                menuButton.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    PingtowerResponse stats = response.body();
                    Log.d("EventChartFragment", "Данные успешно загружены. Events: " + stats.getEvents().size());
                    setupChartWithData(stats);
                    Toast.makeText(requireContext(), "Данные загружены!", Toast.LENGTH_SHORT).show();
                } else {
                    int code = response.code();
                    String errorMsg = "Ошибка загрузки статистики. Код: " + code;
                    Log.e("EventChartFragment", errorMsg);
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PingtowerResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                if (call.isCanceled()) {
                    Log.w("EventChartFragment", "Запрос отменён");
                    return;
                }

                String errorMsg = "Сетевая ошибка: " + t.getMessage();
                Log.e("EventChartFragment", errorMsg, t);
                Toast.makeText(requireContext(), "Не удалось загрузить данные. Проверьте соединение.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupChart() {
        int colorOnSurface = getColorFromAttr(com.google.android.material.R.attr.colorOnSurface);
        int colorSurface = getColorFromAttr(com.google.android.material.R.attr.colorSurface);
        lineChart.getDescription().setEnabled(true);
        lineChart.getDescription().setText("Время отклика сервера");
        lineChart.getDescription().setTextSize(12f);

        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        lineChart.getAxisRight().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(colorOnSurface);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(colorOnSurface);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        lineChart.getLegend().setEnabled(true);
        lineChart.animateX(1000);
    }

    private void setupChartWithData(PingtowerResponse stats) {
        if (stats.getEvents() == null || stats.getEvents().isEmpty()) {
            Toast.makeText(requireContext(), "Нет данных для отображения", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Entry> entries = new ArrayList<>();
        HashSet<String> processedMinutes = new HashSet<>();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat minuteFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (PingtowerResponse.EventDTO event : stats.getEvents()) {
            String timeStr = event.getTime();
            Long responseTimeMs = event.getResponseTimeMs();

            if (responseTimeMs != null && timeStr != null) {
                try {
                    Date date = inputFormat.parse(timeStr);
                    String minuteKey = minuteFormat.format(date);

                    if (!processedMinutes.contains(minuteKey)) {
                        processedMinutes.add(minuteKey);
                        entries.add(new Entry(entries.size(), responseTimeMs.floatValue()));
                    }
                } catch (ParseException e) {
                    Log.w("EventChartFragment", "Ошибка парсинга времени: " + timeStr, e);
                }
            }
        }

        if (entries.isEmpty()) {
            Toast.makeText(requireContext(), "Нет данных о времени отклика", Toast.LENGTH_SHORT).show();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Время отклика (мс)");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(100);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index < 0 || index >= entries.size()) return "";

                int processedIndex = 0;
                for (PingtowerResponse.EventDTO event : stats.getEvents()) {
                    String timeStr = event.getTime();
                    Long responseTimeMs = event.getResponseTimeMs();

                    if (responseTimeMs != null && timeStr != null) {
                        try {
                            Date date = inputFormat.parse(timeStr);
                            String minuteKey = minuteFormat.format(date);

                            if (!processedMinutes.contains(minuteKey)) {
                                // Это не должно случиться, но на всякий случай
                                continue;
                            }

                            if (processedIndex == index) {
                                return displayFormat.format(date);
                            }
                            processedIndex++;
                        } catch (ParseException ignored) {}
                    }
                }
                return "";
            }
        });

        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((int) value) + " мс";
            }
        });

        lineChart.invalidate();

        Log.d("ChartDebug", "Отображаем " + entries.size() + " точек (по одной на минуту)");
    }
    private int getColorFromAttr(int attr) {
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}