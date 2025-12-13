package com.sandul.chefnest.ui.admin.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadDashboardData();

    }

    private void loadDashboardData(){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        new Thread(() -> {
            try {
                JsonObject response = NetworkUtils.makePostRequest("/admin/load-dashboard", "{email: " + email + "}");
                if (response != null && response.get("message").getAsString().equals("success")) {
                    getActivity().runOnUiThread(() -> {
                        TextView userCount = getView().findViewById(R.id.user_count);
                        TextView sellerCount = getView().findViewById(R.id.seller_count);
                        TextView totaUsers = getView().findViewById(R.id.active_users);

                        userCount.setText(response.get("customers").getAsString());
                        sellerCount.setText(response.get("chefs").getAsString());
                        totaUsers.setText(response.get("activeUsers").getAsString());

                        JsonArray userGrowth = response.get("userGrowth").getAsJsonArray();

                        LineChart userGrowthChart = getView().findViewById(R.id.user_chart);
                        setupLineChart(userGrowthChart, getChartData(userGrowth), "User Growth");

                        JsonArray sellerGrowth = response.get("chefsGrowth").getAsJsonArray();

                        LineChart sellerGrowthChart = getView().findViewById(R.id.seller_chart);
                        setupLineChart(sellerGrowthChart, getChartData(sellerGrowth), "Seller Growth");

                    });
                }else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load dashboard data", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void setupLineChart(LineChart chart, List<Entry> data, String label) {
        LineDataSet dataSet = new LineDataSet(data, label);
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return getMonthLabel((int) value);
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularity(1f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.animateX(1000);
        chart.invalidate();
    }

    private List<Entry> getChartData(JsonArray dataArray) {
        List<Entry> data = new ArrayList<>();

        dataArray.forEach((element) -> {
            data.add(new Entry(element.getAsJsonObject().get("month").getAsInt(), element.getAsJsonObject().get("userCount").getAsInt()));
        });
        return data;
    }

    private String getMonthLabel(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month % 12];
    }
}