package com.sandul.chefnest.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sandul.chefnest.databinding.ViewholderAddressBinding;
import com.sandul.chefnest.model.AddressItem;

import java.util.ArrayList;

public class AddressItemAdapter extends RecyclerView.Adapter<AddressItemAdapter.Viewholder> {

    private final ArrayList<AddressItem> addressItems;
    private final OnItemClickListener onItemClickListener;

    public AddressItemAdapter(ArrayList<AddressItem> addressItems, OnItemClickListener onItemClickListener) {
        this.addressItems = addressItems;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AddressItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderAddressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressItemAdapter.Viewholder holder, int position) {
        holder.binding(addressItems.get(position));
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(addressItems.get(position)));
    }

    @Override
    public int getItemCount() {
        return addressItems.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private final ViewholderAddressBinding binding;

        public Viewholder(@NonNull ViewholderAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void binding(AddressItem addressItem) {
            binding.addressItemId.setText(String.valueOf(addressItem.getId()));
            binding.addressItemAddress.setText(addressItem.getAddress());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AddressItem addressItem);
    }
}