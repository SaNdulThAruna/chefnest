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
import com.sandul.chefnest.databinding.ViewholderOrderItemBinding;
import com.sandul.chefnest.model.OrderItem;
import com.sandul.chefnest.ui.customer.activity.OrderDetailsActivity;

import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.Viewholder> {

    private ArrayList<OrderItem> orderItemArrayList;

    public OrderItemAdapter(ArrayList<OrderItem> orderItemArrayList) {
        this.orderItemArrayList = orderItemArrayList;
    }

    @NonNull
    @Override
    public OrderItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderOrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.Viewholder holder, int position) {
        holder.bind(orderItemArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderItemArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private ViewholderOrderItemBinding binding;

        public Viewholder(@NonNull ViewholderOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderItem orderItem) {
            binding.orderItemTitle.setText(orderItem.getOrderTitle());
            binding.orderItemPrice.setText(orderItem.getOrderPrice());

            int statusId = Integer.parseInt(orderItem.getOrderStatusId());

            if (statusId == 1) {
                binding.orderItemStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.gray));
                binding.orderItemStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.gray_light));
            } else if (statusId == 2) {
                binding.orderItemStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.blue));
                binding.orderItemStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.blue_light));
            } else if (statusId == 3) {
                binding.orderItemStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.orange_1));
                binding.orderItemStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.orange_light));
            } else if (statusId == 4) {
                binding.orderItemStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.purple));
                binding.orderItemStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.purple_light));
            } else if (statusId == 5) {
                binding.orderItemStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.pink));
                binding.orderItemStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.pink_light));
            } else if (statusId == 6) {
                binding.orderItemStatus.setTextColor(binding.getRoot().getResources().getColor(R.color.green_1));
                binding.orderItemStatus.setBackgroundTintList(ContextCompat.getColorStateList(this.itemView.getContext(), R.color.dark_green));
            }

            binding.orderItemStatus.setText(orderItem.getOrderStatus());
//            binding.orderItemImg.setImageResource(orderItem.getOrderImg());

            Glide.with(binding.getRoot().getContext())
                    .load(orderItem.getOrderImg())
                    .into(binding.orderItemImg);

            binding.orderItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start OrderDetailsActivity
                    Intent intent = new Intent(v.getContext(), OrderDetailsActivity.class);
                    intent.putExtra("order_id", orderItem.getOrderID());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
