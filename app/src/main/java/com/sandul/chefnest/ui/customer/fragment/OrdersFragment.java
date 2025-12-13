package com.sandul.chefnest.ui.customer.fragment;

import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.OrderItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.OrderItemAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class OrdersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.sandul.chefnest.data", requireActivity().MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "");

                try {
                    JsonObject jsonObject = NetworkUtils.makePostRequest("/load-user-order", "{email:" + email + "}");

                    if (jsonObject.get("message").getAsString().equals("success")) {
                        ArrayList<OrderItem> orderItemArrayList = new ArrayList<>();

                        jsonObject.get("orders").getAsJsonArray().forEach(order -> {
                            JsonObject orderJson = order.getAsJsonObject();
                            orderItemArrayList.add(
                                    new OrderItem(
                                            orderJson.get("orderItemId").getAsInt(),
                                            orderJson.get("foodTitle").getAsString(),
                                            orderJson.get("totalPrice").getAsString(),
                                            orderJson.get("statusId").getAsString(),
                                            orderJson.get("status").getAsString(),
                                            orderJson.get("image1").getAsString()
                                    )
                            );
                        });

                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                RecyclerView recyclerView = requireView().findViewById(R.id.orderItem_recyclerView);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                recyclerView.setAdapter(new OrderItemAdapter(orderItemArrayList));
                            });
                        }
                    } else {
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                } catch (IOException e) {
                    Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
                }
            }
        }).start();

        ImageView backBtn = view.findViewById(R.id.order_fragment_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();

        RecyclerView recyclerView = getView().findViewById(R.id.orderItem_recyclerView);
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
        }
    }*/
}