package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sandul.chefnest.databinding.ViewholderRatingBinding;
import com.sandul.chefnest.model.RatingItem;

import java.util.ArrayList;

public class RatingItemAdapter extends RecyclerView.Adapter<RatingItemAdapter.Viewholder> {

    private ArrayList<RatingItem> ratingItemArrayList;

    public RatingItemAdapter(ArrayList<RatingItem> ratingItemArrayList) {
        this.ratingItemArrayList = ratingItemArrayList;
    }

    @NonNull
    @Override
    public RatingItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderRatingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RatingItemAdapter.Viewholder holder, int position) {
        holder.setBinding(ratingItemArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return ratingItemArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private ViewholderRatingBinding binding;

        public Viewholder(@NonNull ViewholderRatingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setBinding(RatingItem ratingItem) {
            binding.ratingName.setText(ratingItem.getCustomerName());
            binding.ratingContent.setText(ratingItem.getContent());
            binding.ratingBar1.setRating(ratingItem.getRating());
        }
    }
}
