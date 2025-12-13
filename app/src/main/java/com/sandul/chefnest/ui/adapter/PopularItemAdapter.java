package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sandul.chefnest.R;
import com.sandul.chefnest.databinding.ViewholderPopularItemBinding;
import com.sandul.chefnest.model.PopularItem;

import java.util.ArrayList;

public class PopularItemAdapter extends RecyclerView.Adapter<PopularItemAdapter.Viewholder> {

    ArrayList<PopularItem> popularItemsList;

    public PopularItemAdapter(ArrayList<PopularItem> popularItemsList) {
        this.popularItemsList = popularItemsList;
    }

    @NonNull
    @Override
    public PopularItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderPopularItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PopularItemAdapter.Viewholder holder, int position) {
        holder.bind(popularItemsList.get(position));

    }

    @Override
    public int getItemCount() {
        return popularItemsList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private final ViewholderPopularItemBinding binding;

        public Viewholder(@NonNull ViewholderPopularItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PopularItem popularItem) {
//            binding.popularItemImg.setImageResource(popularItem.getImage());
            binding.popularItemTitle.setText(popularItem.getTitle());

            Glide.with(binding.getRoot())
                    .load(popularItem.getImage())
                    .into(binding.popularItemImg);

        }
    }
}
