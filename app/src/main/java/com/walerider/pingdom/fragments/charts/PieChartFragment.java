package com.walerider.pingdom.fragments.charts;

import android.graphics.Color;
import android.os.Bundle;

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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PieChartFragment extends Fragment {

    private PieChart pieChart;
    private ProgressBar progressBar;
    private Long siteId;
    private ImageButton menuButton;

    public PieChartFragment() {
        // Required empty public constructor
    }

    public static PieChartFragment newInstance(String param1, String param2) {
        PieChartFragment fragment = new PieChartFragment();
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
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        pieChart = view.findViewById(R.id.pieChartView);
        menuButton = view.findViewById(R.id.menuButtonPie);

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
    private void setupChart() {
        int colorOnSurface = getColorFromAttr(com.google.android.material.R.attr.colorOnSurface);
        menuButton.setVisibility(View.VISIBLE);
        pieChart.getDescription().setEnabled(true);
        pieChart.getDescription().setText("Доступность сайта");
        pieChart.getDescription().setTextSize(12f);
        pieChart.getDescription().setTextColor(colorOnSurface);

        pieChart.setTouchEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(colorOnSurface);

        pieChart.animateY(1000, Easing.EaseInOutQuad);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setTextColor(colorOnSurface);
    }

    private void setupChartWithData(PingtowerResponse stats) {
        Double uptimePercent = stats.getUptimePercent();
        Double downTimePercent = stats.getDownTimePercent();

        if (uptimePercent == null || downTimePercent == null) {
            Toast.makeText(requireContext(), "Нет данных о доступности", Toast.LENGTH_SHORT).show();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(uptimePercent.floatValue(), "Доступен"));
        entries.add(new PieEntry(downTimePercent.floatValue(), "Недоступен"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4CAF50"));
        colors.add(Color.parseColor("#F44336"));
        dataSet.setColors(colors);

        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(getColorFromAttr(com.google.android.material.R.attr.colorOnSurface));
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.1f%%", value);
            }
        });

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.invalidate();

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
    private int getColorFromAttr(int attr) {
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}