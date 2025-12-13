package com.sandul.chefnest.ui.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sandul.chefnest.R;
import com.sandul.chefnest.databinding.ViewholderFoodItemBinding;
import com.sandul.chefnest.model.FoodItem;
import com.sandul.chefnest.ui.customer.activity.SingleProductActivity;

import java.util.ArrayList;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.Viewholder> {

    private ArrayList<FoodItem> foodItemsList;

    public FoodItemAdapter(ArrayList<FoodItem> foodItemsList) {
        this.foodItemsList = foodItemsList;
    }

    @NonNull
    @Override
    public FoodItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderFoodItemBinding binding = ViewholderFoodItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemAdapter.Viewholder holder, int position) {
        FoodItem foodItem = foodItemsList.get(position);
        holder.bind(foodItem);
    }

    @Override
    public int getItemCount() {
        return foodItemsList.size();
    }

    public void updateData(ArrayList<FoodItem> newFoodItems) {
        foodItemsList.clear();
        foodItemsList.addAll(newFoodItems);
        notifyDataSetChanged();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private final ViewholderFoodItemBinding binding;

        public Viewholder(@NonNull ViewholderFoodItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FoodItem foodItem) {
            binding.foodItemTitle.setText(foodItem.getFoodItemTitle());
            binding.foodItemPrice.setText(foodItem.getFoodItemPrice());
            binding.foodItemLocation.setText(foodItem.getFoodItemLocation());

            String imageUrl = foodItem.getFoodItemImage();
//            Log.d("ChefNestLog", "Loading image URL: " + imageUrl);

            if (imageUrl.startsWith("http://")) {
                imageUrl = imageUrl.replace("http://", "https://");
            }

            Glide.with(binding.getRoot().getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions().transform(new RoundedCorners(16)))
                    .into(binding.foodItemImage);


            binding.foodItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), SingleProductActivity.class);
                    intent.putExtra("id", foodItem.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
