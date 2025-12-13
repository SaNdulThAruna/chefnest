package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sandul.chefnest.R;
import com.sandul.chefnest.databinding.ViewholderUserItemBinding;
import com.sandul.chefnest.model.UserItem;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {

    private ArrayList<UserItem> userItemArrayList;
    private OnUserItemClickListener onUserItemClickListener;

    public UserAdapter(ArrayList<UserItem> userItemArrayList, OnUserItemClickListener onUserItemClickListener) {
        this.userItemArrayList = userItemArrayList;
        this.onUserItemClickListener = onUserItemClickListener;
    }

    @NonNull
    @Override
    public UserAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderUserItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.Viewholder holder, int position) {
        holder.bind(userItemArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return userItemArrayList.size();
    }

    public interface OnUserItemClickListener {
        void onUserItemClick(UserItem userItem);
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private ViewholderUserItemBinding binding;

        public Viewholder(@NonNull ViewholderUserItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(UserItem userItem) {
            binding.userName.setText(userItem.getUserName());
            binding.userEmail.setText(userItem.getUserEmail());

            if (userItem.getUserStatus() == 1) {
                binding.userStatus.setText("Active");
                binding.userStatus.setTextColor(binding.getRoot().getContext().getResources().getColor(R.color.dark_green));
                binding.userStatus.setBackgroundTintList(binding.getRoot().getContext().getResources().getColorStateList(R.color.green_1));
            } else {
                binding.userStatus.setText("Inactive");
                binding.userStatus.setTextColor(binding.getRoot().getContext().getResources().getColor(R.color.white));
                binding.userStatus.setBackgroundTintList(binding.getRoot().getContext().getResources().getColorStateList(R.color.light_red));
            }

            if (userItem.getUserImage() != null && !userItem.getUserImage().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(userItem.getUserImage())
                        .into(binding.userImg);
            } else {
                binding.userImg.setImageResource(R.drawable.user_back2);
            }

            binding.userStatus.setOnClickListener(v -> onUserItemClickListener.onUserItemClick(userItem));
        }
    }
}