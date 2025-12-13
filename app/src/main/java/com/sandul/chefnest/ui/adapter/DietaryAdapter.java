package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sandul.chefnest.databinding.ViewholderDietaryItemBinding;
import com.sandul.chefnest.model.DietaryItem;

import java.util.ArrayList;

public class DietaryAdapter extends RecyclerView.Adapter<DietaryAdapter.Viewholder> {

    ArrayList<DietaryItem> dietaryItemsList;

    public DietaryAdapter(ArrayList<DietaryItem> dietaryItemsList) {
        this.dietaryItemsList = dietaryItemsList;
    }


    @NonNull
    @Override
    public DietaryAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderDietaryItemBinding binding = ViewholderDietaryItemBinding.inflate(inflater, parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DietaryAdapter.Viewholder holder, int position) {
        DietaryItem dietaryItem = dietaryItemsList.get(position);
        holder.binding.dietText.setText(dietaryItem.getName());
//        holder.binding.dietImg.setImageResource(dietaryItem.getResourceId());

        Glide.with(holder.itemView.getContext())
                .load(dietaryItem.getResourceId())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.binding.dietImg);

    }

    @Override
    public int getItemCount() {
        return dietaryItemsList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        private final ViewholderDietaryItemBinding binding;

        public Viewholder(ViewholderDietaryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
