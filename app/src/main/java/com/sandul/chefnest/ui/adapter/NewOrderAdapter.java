package com.sandul.chefnest.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sandul.chefnest.databinding.ViewholderNewOrdersBinding;
import com.sandul.chefnest.model.SellerNewOrder;
import com.sandul.chefnest.network.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

public class NewOrderAdapter extends RecyclerView.Adapter<NewOrderAdapter.Viewholder> {

    private ArrayList<SellerNewOrder> sellerNewOrderArrayList;

    public NewOrderAdapter(ArrayList<SellerNewOrder> sellerNewOrderArrayList) {
        this.sellerNewOrderArrayList = sellerNewOrderArrayList;
    }

    @NonNull
    @Override
    public NewOrderAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderNewOrdersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewOrderAdapter.Viewholder holder, int position) {
        holder.bind(sellerNewOrderArrayList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return sellerNewOrderArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private final ViewholderNewOrdersBinding binding;

        public Viewholder(@NonNull ViewholderNewOrdersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SellerNewOrder sellerNewOrder, int position) {
            binding.newOrderTitle.setText(sellerNewOrder.getTitle());
            binding.newOrderQty.setText(sellerNewOrder.getQty());
            binding.newOrderId.setText(sellerNewOrder.getId());

            Glide.with(binding.getRoot().getContext())
                    .load(sellerNewOrder.getImg())
                    .into(binding.newOrderImg);

            binding.newOrderBtn.setOnClickListener(v -> {
                new Thread(() -> {
                    SharedPreferences sharedPreferences = binding.getRoot().getContext().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                    String email = sharedPreferences.getString("email", "");

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("email", email);
                    jsonObject.addProperty("id", sellerNewOrder.getId());
                    jsonObject.addProperty("status", 2);

                    try {
                        JsonObject responseJson = NetworkUtils.makePostRequest("/update-order-status", jsonObject.toString());

                        if (responseJson.get("message").getAsString().equals("success")) {
                            ((Activity) binding.getRoot().getContext()).runOnUiThread(() -> {
                                // Remove item from the list
                                sellerNewOrderArrayList.remove(position);
                                // Notify adapter about item removal
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, sellerNewOrderArrayList.size());

                                // Show message dialog
                                new AlertDialog.Builder(binding.getRoot().getContext())
                                        .setTitle("Order Accepted")
                                        .setMessage("The order has been accepted successfully.")
                                        .setPositiveButton(android.R.string.ok, null)
                                        .show();
                            });
                        } else {
                            Log.e("NewOrderAdapter", "Error updating order status: " + responseJson.get("message").getAsString());
                        }
                    } catch (IOException e) {
                        Log.e("NewOrderAdapter", "Error updating order status", e);
                    }
                }).start();
            });
        }
    }
}