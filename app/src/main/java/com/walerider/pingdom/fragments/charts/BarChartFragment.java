package com.walerider.pingdom.fragments.charts;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.walerider.pingdom.R;
import com.walerider.pingdom.api.API;
import com.walerider.pingdom.api.APIClient;
import com.walerider.pingdom.api.entitys.PingtowerResponse;
import com.walerider.pingdom.utils.TokenStorage;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarChartFragment extends Fragment {
    private BarChart barChart;
    private ProgressBar progressBar;
    private Long siteId;
    private ImageButton menuButton;
    public BarChartFragment() {
    }
    public static BarChartFragment newInstance(String param1, String param2) {
        BarChartFragment fragment = new BarChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            siteId = getArguments().getLong("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        barChart = view.findViewById(R.id.barChartView);
        menuButton =view.findViewById(R.id.menuButtonBar);
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
        Log.e("date",dateTo);
        try {
            loadSiteStats(siteId,dateFrom,dateTo);
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

        barChart.getDescription().setEnabled(true);
        barChart.getDescription().setText("Распределение HTTP-кодов");
        barChart.getDescription().setTextSize(12f);

        barChart.setTouchEnabled(true);
        barChart.setPinchZoom(false); // Для BarChart pinch zoom обычно не нужен
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(colorOnSurface);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(colorOnSurface);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(true);

        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000);
    }

    private void setupChartWithData(PingtowerResponse stats) {
        Map<String, PingtowerResponse.HttpCodeInfo> httpCodes = stats.getHttpCodes();
        if (httpCodes == null || httpCodes.isEmpty()) {
            Toast.makeText(requireContext(), "Нет данных для отображения", Toast.LENGTH_SHORT).show();
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, PingtowerResponse.HttpCodeInfo> entry : httpCodes.entrySet()) {
            String httpCode = entry.getKey();
            PingtowerResponse.HttpCodeInfo info = entry.getValue();
            if (info != null) {
                entries.add(new BarEntry(index, info.getCount()));
                labels.add(httpCode);
                index++;
            }
        }

        if (entries.isEmpty()) {
            Toast.makeText(requireContext(), "Нет данных о HTTP-кодах", Toast.LENGTH_SHORT).show();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Количество запросов");
        dataSet.setValueTextColor(getColorFromAttr(com.google.android.material.R.attr.colorOnSurface));
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(true);
        Legend legend = barChart.getLegend();
        legend.setTextColor(getColorFromAttr(com.google.android.material.R.attr.colorPrimaryInverse)); // цвет текста легенды
        legend.setTextSize(12f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                return String.valueOf((int) value);
            }
        });

        List<Double> percents = new ArrayList<>();
        for (PingtowerResponse.HttpCodeInfo info : httpCodes.values()) {
            if (info != null) {
                percents.add(info.getPercent());
            }
        }

        dataSet.setValueFormatter(new ValueFormatter() {

            public String getFormattedValue(float value, Entry entry) {
                int index = (int) entry.getX();
                if (index >= 0 && index < percents.size()) {
                    double percent = percents.get(index);
                    return String.format(Locale.getDefault(), "%d\n(%.1f%%)", (int) entry.getY(), percent);
                }
                return String.valueOf((int) entry.getY());
            }
        });


        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);
        barData.setValueTextColor(getColorFromAttr(com.google.android.material.R.attr.colorPrimaryInverse));

        barChart.setData(barData);


        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value);
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });


        barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        barChart.invalidate();

        Log.d("ChartDebug", "Отображаем " + entries.size() + " HTTP-кодов");
    }
    private int getColorFromAttr(int attr) {
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}