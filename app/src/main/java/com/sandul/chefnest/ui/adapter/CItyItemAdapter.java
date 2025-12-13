package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sandul.chefnest.databinding.ViewholderSearchTextBinding;
import com.sandul.chefnest.model.CityItem;

import java.util.List;

public class CItyItemAdapter extends RecyclerView.Adapter<CItyItemAdapter.Viewholder> {

    private List<CityItem> cityItemsList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnItemClickListener onItemClickListener;

    public CItyItemAdapter(List<CityItem> cityItemsList) {
        this.cityItemsList = cityItemsList;
    }

    @NonNull
    @Override
    public CItyItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderSearchTextBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CItyItemAdapter.Viewholder holder, int position) {
        holder.bind(cityItemsList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return cityItemsList.size();
    }

    public int getSelectedCityId() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return cityItemsList.get(selectedPosition).getId();
        }
        return -1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateList(List<CityItem> newList) {
        cityItemsList = newList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(CityItem cityItem);
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private ViewholderSearchTextBinding binding;

        public Viewholder(@NonNull ViewholderSearchTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CityItem cityItem, int position) {
            binding.cityText.setText(cityItem.getCityName());
            binding.selectedImg.setVisibility(position == selectedPosition ? View.VISIBLE : View.INVISIBLE);

            itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(cityItem);
                }
            });
        }
    }
}