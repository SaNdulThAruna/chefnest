package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.sandul.chefnest.R;
import com.sandul.chefnest.databinding.ViewholderNotificationItemBinding;
import com.sandul.chefnest.model.NotificationItem;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Viewholder> {

    private ArrayList<NotificationItem> notificationItemsList;

    public NotificationAdapter(ArrayList<NotificationItem> notificationItemsList) {
        this.notificationItemsList = notificationItemsList;
    }

    @NonNull
    @Override
    public NotificationAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderNotificationItemBinding binding = ViewholderNotificationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.Viewholder holder, int position) {
        holder.bind(notificationItemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationItemsList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        private final ViewholderNotificationItemBinding binding;

        public Viewholder(@NonNull ViewholderNotificationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NotificationItem notificationItem){
            binding.notificationTitle.setText(notificationItem.getNotificationTitle());
            binding.notificationContent.setText(notificationItem.getNotificationContent());
            binding.notificationDate.setText(notificationItem.getNotificationDate());
//            binding.notificationImg.setImageResource(notificationItem.getNotificationImage());

            Glide.with(binding.getRoot().getContext())
                    .load(notificationItem.getNotificationImage())
                    .into(binding.notificationImg);

        }
    }
}
