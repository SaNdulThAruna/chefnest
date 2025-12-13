package com.sandul.chefnest.ui.seller.fragment;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.SellerOrderItem;
import com.sandul.chefnest.model.SpinnerItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.SellerOrderItemAdapter;
import com.sandul.chefnest.ui.adapter.SpinnerItemAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class SellerOrderFragment extends Fragment {

    private ImageView allOrderLine, pendingLine, pickupLine, deliverLine;
    private TextView allText, pendingText, pickupText, deliverText;

    private int statusId = 0;
    private int filterId = 0;
    private SellerOrderItemAdapter sellerOrderItemAdapter;
    private ArrayList<SellerOrderItem> orderItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView orderRecyclerView = view.findViewById(R.id.seller_order_item_recyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        sellerOrderItemAdapter = new SellerOrderItemAdapter(orderItems);
        orderRecyclerView.setAdapter(sellerOrderItemAdapter);

        // Get the status ID from the arguments
        if (getArguments() != null) {
            statusId = getArguments().getInt("statusId", 0);
            loadOrders(statusId, filterId); // Load orders automatically
        }else {
            loadOrders(statusId, filterId); // Load orders automatically
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ConstraintLayout allOrder = getView().findViewById(R.id.allOrder_layout);
        allOrderLine = getView().findViewById(R.id.allOrder_line);
        ConstraintLayout pending = getView().findViewById(R.id.pending_layout);
        pendingLine = getView().findViewById(R.id.pending_line);
        ConstraintLayout pickup = getView().findViewById(R.id.pickup_layout);
        pickupLine = getView().findViewById(R.id.pickup_line);
        ConstraintLayout deliver = getView().findViewById(R.id.delivered_layout);
        deliverLine = getView().findViewById(R.id.delivered_line);

        allText = getView().findViewById(R.id.allOrder_text);
        pendingText = getView().findViewById(R.id.pending_text);
        pickupText = getView().findViewById(R.id.pickup_text);
        deliverText = getView().findViewById(R.id.delivered_text);

        // Set default selection to "All Orders"
        updateLineVisibility(allOrderLine, allText);

        allOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLineVisibility(allOrderLine, allText);
                statusId = 0;
                loadOrders(statusId, filterId);
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLineVisibility(pendingLine, pendingText);
                statusId = 2;
                loadOrders(statusId, filterId);
            }
        });

        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLineVisibility(pickupLine, pickupText);
                statusId = 4;
                loadOrders(statusId, filterId);
            }
        });

        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLineVisibility(deliverLine, deliverText);
                statusId = 6;
                loadOrders(statusId, filterId);
            }
        });

        Spinner spinner = getView().findViewById(R.id.date_spinner);

        SpinnerItemAdapter priceSpinnerAdapter = new SpinnerItemAdapter(getContext(), R.layout.viewholder_spinner_item, new ArrayList<>() {
            {
                add(new SpinnerItem(0, "Select Date"));
                add(new SpinnerItem(1, "Newest"));
                add(new SpinnerItem(2, "Oldest"));
            }
        });

        spinner.setAdapter(priceSpinnerAdapter);
        spinner.setSelection(0);
        spinner.setDropDownVerticalOffset(100);
        spinner.setBackground(null);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem item = (SpinnerItem) parent.getItemAtPosition(position);
                // Load orders based on the selected item
                if (item.getId() != 0) {
                    filterId = item.getId();
                    loadOrders(statusId, filterId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        switch (statusId) {
            case 4:
                updateLineVisibility(pickupLine, pickupText);
                break;
            case 2:
                updateLineVisibility(pendingLine, pendingText);
                break;
            case 6:
                updateLineVisibility(deliverLine, deliverText);
                break;
            default:
                updateLineVisibility(allOrderLine, allText);
                break;
        }
    }

    private void loadOrders(int statusId, int filterId) {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/filter-orders", "{\"email\":\"" + email + "\",\"status\":\"" + statusId + "\",\"filter\":\"" + filterId + "\"}");

                if (jsonObject.get("message").getAsString().equals("success")) {
                    ArrayList<SellerOrderItem> newOrderItems = new ArrayList<>();

                    jsonObject.get("orders").getAsJsonArray().forEach(order -> {
                        newOrderItems.add(new SellerOrderItem(
                                order.getAsJsonObject().get("title").getAsString(),
                                order.getAsJsonObject().get("id").getAsString(),
                                order.getAsJsonObject().get("quantity").getAsString(),
                                order.getAsJsonObject().get("price").getAsString(),
                                order.getAsJsonObject().get("statusId").getAsString(),
                                order.getAsJsonObject().get("status").getAsString(),
                                order.getAsJsonObject().get("customer").getAsString(),
                                order.getAsJsonObject().get("city").getAsString(),
                                order.getAsJsonObject().get("image1").getAsString()
                        ));
                    });

                    getActivity().runOnUiThread(() -> {
                        orderItems.clear();
                        orderItems.addAll(newOrderItems);
                        sellerOrderItemAdapter.notifyDataSetChanged();
                    });
                }
            } catch (IOException e) {
                getActivity().runOnUiThread(() -> {
                    Log.e("SellerOrderFragment", "Error loading orders", e);
                });
            }
        }).start();
    }

    private void updateLineVisibility(ImageView visibleLine, TextView visibleText) {
        allOrderLine.setVisibility(View.INVISIBLE);
        pendingLine.setVisibility(View.INVISIBLE);
        pickupLine.setVisibility(View.INVISIBLE);
        deliverLine.setVisibility(View.INVISIBLE);

        allText.setTextColor(getResources().getColor(R.color.gray));
        pendingText.setTextColor(getResources().getColor(R.color.gray));
        pickupText.setTextColor(getResources().getColor(R.color.gray));
        deliverText.setTextColor(getResources().getColor(R.color.gray));

        // Animate the visibility change
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(visibleLine, "alpha", 0f, 1f);
        fadeIn.setDuration(500); // duration in milliseconds
        fadeIn.start();

        visibleLine.setVisibility(View.VISIBLE);
        visibleText.setTextColor(getResources().getColor(R.color.black));
    }
}