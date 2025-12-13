package com.sandul.chefnest.ui.adapter;

import android.content.Intent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sandul.chefnest.databinding.ViewholderReviewBinding;
import com.sandul.chefnest.model.ReviewItem;
import com.sandul.chefnest.ui.customer.activity.ReviewsActivity;

import java.util.ArrayList;

public class ReviewsItemAdapter extends RecyclerView.Adapter<ReviewsItemAdapter.Viewholder> {

    private ArrayList<ReviewItem> reviewItemList;

    public ReviewsItemAdapter(ArrayList<ReviewItem> reviewItemList) {
        this.reviewItemList = reviewItemList;
    }

    @NonNull
    @Override
    public ReviewsItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsItemAdapter.Viewholder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return reviewItemList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private ViewholderReviewBinding binding;

        public Viewholder(@NonNull ViewholderReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ReviewItem reviewItem) {
            binding.reviewTitle.setText(reviewItem.getTitle());
            binding.reviewPrice.setText(reviewItem.getPrice());

            Glide.with(binding.getRoot())
                    .load(reviewItem.getImg())
                    .into(binding.reviewImg);

            binding.reviewButton.setOnClickListener(v->{

                Intent intent = new Intent(binding.getRoot().getContext(), ReviewsActivity.class);
                intent.putExtra("id", reviewItem.getId());
                v.getContext().startActivity(intent);

            });

        }
    }
}
