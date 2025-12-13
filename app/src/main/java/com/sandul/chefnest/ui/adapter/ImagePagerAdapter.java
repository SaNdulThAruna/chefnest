package com.sandul.chefnest.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sandul.chefnest.R;
import com.sandul.chefnest.databinding.ViewholderOrderItemBinding;
import com.sandul.chefnest.databinding.ViewholderViewpager2Binding;
import com.sandul.chefnest.ui.customUI.RoundedImageView;

import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {

    private List<String> imageUrls;

    public ImagePagerAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new ViewHolder(ViewholderViewpager2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(imageUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewholderViewpager2Binding binding;

        public ViewHolder(@NonNull ViewholderViewpager2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String urls){
            Glide.with(binding.getRoot().getContext())
                    .load(urls)
                    .into(binding.dishImage);
        }
    }
}
