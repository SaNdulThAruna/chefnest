package com.sandul.chefnest.ui.seller.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.SellerNewOrder;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.NewOrderAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class SellerDashboardFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadDashboardData();
        loadChartData();
        loadNewOrders();

    }

    private void loadDashboardData() {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
        new Thread(() -> {


            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-dashboard-data", "{\"email\":\"" + email + "\"}");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    getActivity().runOnUiThread(() -> {

                        TextView sales = getView().findViewById(R.id.sales_textView);
                        TextView orders = getView().findViewById(R.id.order_count);
                        TextView pending = getView().findViewById(R.id.pending_count);

                        sales.setText(jsonObject.get("todaySales").getAsString());
                        orders.setText(jsonObject.get("totalOrders").getAsString());
                        pending.setText(jsonObject.get("pendingOrders").getAsString());

                    });

                } else {
                    Log.e("SellerDashboardFragment", "Error loading dashboard data: " + jsonObject.get("message").getAsString());
                }

            } catch (IOException e) {
                Log.e("SellerDashboardFragment", "Error loading dashboard data", e);
            }

        }).start();

    }

    private void loadChartData() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-product-sales", "{\"email\":\"" + email + "\"}");

                if (jsonObject.get("message").getAsString().equals("success")) {
                    ArrayList<Entry> entries = new ArrayList<>();

                    jsonObject.getAsJsonArray("earnings").forEach(data -> {
                        entries.add(new Entry(data.getAsJsonObject().get("month").getAsInt(), data.getAsJsonObject().get("earnings").getAsFloat()));
                    });

                    getActivity().runOnUiThread(() -> {
                        LineChart lineChart = getView().findViewById(R.id.sales_chart);

                        LineDataSet dataSet = new LineDataSet(entries, "Monthly Sales");
                        dataSet.setColor(getContext().getColor(R.color.orange_1));
                        dataSet.setValueTextColor(Color.BLACK);
                        dataSet.setLineWidth(2f);
                        dataSet.setCircleColor(getContext().getColor(R.color.orange_1));
                        dataSet.setCircleRadius(5f);
                        dataSet.setDrawCircleHole(true);
                        dataSet.setCircleHoleColor(Color.YELLOW);
                        dataSet.setDrawFilled(true);
                        dataSet.setFillColor(getContext().getColor(R.color.orange_1));
                        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

                        LineData lineData = new LineData(dataSet);
                        lineChart.setData(lineData);

                        // Customize X-Axis
                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);
                        xAxis.setLabelCount(12);
                        xAxis.setDrawGridLines(false);

                        // Customize Y-Axis
                        YAxis leftAxis = lineChart.getAxisLeft();
                        leftAxis.setDrawGridLines(false);
                        leftAxis.setAxisMinimum(0f); // Set minimum value to 0
                        YAxis rightAxis = lineChart.getAxisRight();
                        rightAxis.setEnabled(false);

                        // Customize Legend
                        Legend legend = lineChart.getLegend();
                        legend.setTextSize(12f);
                        legend.setForm(Legend.LegendForm.LINE);

                        // Customize Description
                        Description description = new Description();
                        description.setText("");
                        description.setTextSize(12f);
                        lineChart.setDescription(description);

                        // Disable zooming
                        lineChart.setScaleEnabled(false);
                        lineChart.setPinchZoom(false);

                        // Animate the chart
                        lineChart.animateX(2000, Easing.EaseInOutCubic);
                    });

                }
            } catch (IOException e) {
                Log.e("SellerDashboardFragment", "Error loading chart data", e);
            }
        }).start();
    }

    private void loadNewOrders(){

        new Thread(()->{

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-new-orders", "{\"email\":\"" + email + "\"}");

                if (jsonObject.get("message").getAsString().equals("success")) {
                    ArrayList<SellerNewOrder> sellerNewOrderArrayList = new ArrayList<>();

                    jsonObject.getAsJsonArray("orders").forEach(data -> {
                        sellerNewOrderArrayList.add(new SellerNewOrder(data.getAsJsonObject().get("id").getAsString(), data.getAsJsonObject().get("title").getAsString(), data.getAsJsonObject().get("qty").getAsString(), data.getAsJsonObject().get("image1").getAsString()));
                    });

                    getActivity().runOnUiThread(() -> {
                        RecyclerView newOrderRecyclerView = getView().findViewById(R.id.newOrder_recyclerView);
                        newOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        newOrderRecyclerView.setAdapter(new NewOrderAdapter(sellerNewOrderArrayList));
                    });
                }else {
                    getActivity().runOnUiThread(() -> {
                        Log.e("SellerDashboardFragment", "Error loading new orders: " + jsonObject.get("message").getAsString());
                    });
                }
            } catch (IOException e) {
                Log.e("SellerDashboardFragment", "Error loading new orders", e);
            }

        }).start();

    }
}