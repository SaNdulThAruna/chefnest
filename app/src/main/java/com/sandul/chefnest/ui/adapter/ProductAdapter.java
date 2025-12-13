package com.sandul.chefnest.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sandul.chefnest.databinding.ViewholderSellerProductItemBinding;
import com.sandul.chefnest.model.Product;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.seller.activity.AddProductActivity;

import java.io.IOException;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.Viewholder> {

    private final ArrayList<Product> productArrayList;

    public ProductAdapter(ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }

    @NonNull
    @Override
    public ProductAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderSellerProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.Viewholder holder, int position) {
        holder.bind(productArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private final ViewholderSellerProductItemBinding binding;

        public Viewholder(@NonNull ViewholderSellerProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product) {
            binding.productTitle.setText(product.getProductTitle());
            binding.productPrice.setText(String.format("Rs.%s", product.getProductPrice()));

            Glide.with(binding.getRoot().getContext())
                    .load(product.getProductImg())
                    .timeout(6000)
                    .into(binding.productImg);

            binding.productEdit.setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), AddProductActivity.class);
                intent.putExtra("productId", product.getProductId());
                binding.getRoot().getContext().startActivity(intent);
            });

            binding.productDelete.setOnClickListener(
                    v -> deleteDish(product.getProductId(), getAdapterPosition())
            );
        }

        private void deleteDish(int productId, int position) {
            new Thread(() -> {
                SharedPreferences sharedPreferences = binding.getRoot().getContext().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "");

                try {
                    JsonObject jsonObject = NetworkUtils.makePostRequest("/delete-dish", "{\"email\":\"" + email + "\",\"id\":\"" + productId + "\"}");

                    if (jsonObject.get("message").getAsString().equals("success")) {
                        binding.getRoot().post(() -> {
                            productArrayList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, productArrayList.size());
                        });
                    } else {
                        binding.getRoot().post(() -> Log.e("ProductAdapter", "Error deleting dish: " + jsonObject.get("message").getAsString()));
                    }
                } catch (IOException e) {
                    binding.getRoot().post(() -> Log.e("ProductAdapter", "Error deleting dish: " + e.getMessage()));
                }
            }).start();
        }
    }
}