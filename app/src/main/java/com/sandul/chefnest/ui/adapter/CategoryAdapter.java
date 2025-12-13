package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sandul.chefnest.databinding.ViewholderCategoryBinding;
import com.sandul.chefnest.databinding.ViewholderUserItemBinding;
import com.sandul.chefnest.model.CategoryItem;
import com.sandul.chefnest.model.UserItem;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Viewholder> {

    private ArrayList<CategoryItem> categoryArrayList;

    public CategoryAdapter(ArrayList<CategoryItem> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public CategoryAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.Viewholder holder, int position) {
        holder.bind(categoryArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private ViewholderCategoryBinding binding;

        public Viewholder(@NonNull ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CategoryItem categoryItem) {

            binding.catName.setText(categoryItem.getCategoryName());
            binding.catId.setText(String.valueOf(categoryItem.getId()));
//            binding.catImg.setImageResource(categoryItem.getCategoryImage());

            Glide.with(binding.getRoot().getContext())
                    .load(categoryItem.getCategoryImage())
                    .into(binding.catImg);

        }
    }
}
