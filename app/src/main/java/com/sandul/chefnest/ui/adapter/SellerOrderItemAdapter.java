package com.sandul.chefnest.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sandul.chefnest.R;
import com.sandul.chefnest.databinding.ViewholderSellerOrderItemBinding;
import com.sandul.chefnest.model.SellerOrderItem;
import com.sandul.chefnest.ui.seller.activity.SellerOrderDetailsActivity;

import java.util.ArrayList;

public class SellerOrderItemAdapter extends RecyclerView.Adapter<SellerOrderItemAdapter.Viewholder> {

    private ArrayList<SellerOrderItem> sellerOrderItemArrayList;

    public SellerOrderItemAdapter(ArrayList<SellerOrderItem> sellerOrderItemArrayList) {
        this.sellerOrderItemArrayList = sellerOrderItemArrayList;
    }

    @NonNull
    @Override
    public SellerOrderItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderSellerOrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SellerOrderItemAdapter.Viewholder holder, int position) {
        holder.bind(sellerOrderItemArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return sellerOrderItemArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private final ViewholderSellerOrderItemBinding binding;

        public Viewholder(@NonNull ViewholderSellerOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SellerOrderItem sellerOrderItem) {
            binding.sellerOrderId.setText(String.format("#%s", sellerOrderItem.getOrderId()));
            binding.sellerOrderTitle.setText(sellerOrderItem.getOrderTitle());
            binding.sellerOrderPrice.setText(String.format("Rs.%s", sellerOrderItem.getOrderPrice()));
            binding.sellerOrderQty.setText(sellerOrderItem.getOrderQty());

            int statusId = Integer.parseInt(sellerOrderItem.getOrderStatusId());

            if (statusId == 1) {
                binding.sellerOrderStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.gray));
                binding.sellerOrderStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.gray_light));
            } else if (statusId == 2) {
                binding.sellerOrderStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.blue));
                binding.sellerOrderStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.blue_light));
            } else if (statusId == 3) {
                binding.sellerOrderStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.orange_1));
                binding.sellerOrderStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.orange_light));
            } else if (statusId == 4) {
                binding.sellerOrderStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.purple));
                binding.sellerOrderStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.purple_light));
            } else if (statusId == 5) {
                binding.sellerOrderStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.pink));
                binding.sellerOrderStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.pink_light));
            } else if (statusId == 6) {
                binding.sellerOrderStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.green_1));
                binding.sellerOrderStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.dark_green));
            }

            binding.sellerOrderStatus.setText(sellerOrderItem.getOrderStatus());
            binding.sellerOrderCustomerName.setText(sellerOrderItem.getCustomerName());
            binding.sellerOrderCityCode.setText(sellerOrderItem.getCustomerPostalCodeCityName());
//            binding.sellerOrderImg.setImageResource(sellerOrderItem.getOrderImg());

            Glide.with(binding.getRoot().getContext())
                    .load(sellerOrderItem.getOrderImg())
                    .into(binding.sellerOrderImg);

            binding.sellerOrderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), SellerOrderDetailsActivity.class);
                    intent.putExtra("orderId", sellerOrderItem.getOrderId());
                    v.getContext().startActivity(intent);

                }
            });
        }
    }
}
