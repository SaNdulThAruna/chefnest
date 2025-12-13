package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sandul.chefnest.databinding.ViewholderCheckoutItemBinding;
import com.sandul.chefnest.model.CheckoutItem;

import java.util.ArrayList;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.Viewholder> {

    private final ArrayList<CheckoutItem> checkoutItems;

    public CheckoutItemAdapter(ArrayList<CheckoutItem> checkoutItems) {
        this.checkoutItems = checkoutItems;
    }

    @NonNull
    @Override
    public CheckoutItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderCheckoutItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemAdapter.Viewholder holder, int position) {
        holder.binding(checkoutItems.get(position));
    }

    @Override
    public int getItemCount() {
        return checkoutItems.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private final ViewholderCheckoutItemBinding binding;

        public Viewholder(@NonNull ViewholderCheckoutItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void binding(CheckoutItem checkoutItem) {

            binding.chTitle.setText(checkoutItem.getTitle());
            binding.chPrice.setText(String.format("Rs. %s", String.valueOf(checkoutItem.getPrice())));
            binding.chQty.setText(String.valueOf(checkoutItem.getQty()));
            Glide.with(binding.getRoot()).load(checkoutItem.getImg()).into(binding.chImg);
        }

    }
}
