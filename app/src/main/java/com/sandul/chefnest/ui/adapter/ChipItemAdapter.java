package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sandul.chefnest.R;
import com.sandul.chefnest.databinding.ViewholderChipLayoutBinding;
import com.sandul.chefnest.model.ChipItem;

import java.util.ArrayList;

public class ChipItemAdapter extends RecyclerView.Adapter<ChipItemAdapter.Viewholder> {

    private final ArrayList<ChipItem> chipItemArrayList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnItemClickListener onItemClickListener;

    public ChipItemAdapter(ArrayList<ChipItem> chipItemArrayList) {
        this.chipItemArrayList = chipItemArrayList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ChipItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderChipLayoutBinding binding = ViewholderChipLayoutBinding.inflate(inflater, parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        ChipItem chipItem = chipItemArrayList.get(position);
        holder.binding.chipText.setText(chipItem.getName());
        holder.binding.chipConstrainLayout.setBackgroundResource(
                position == selectedPosition ? R.drawable.chip_selected : R.drawable.chip_unselected
        );

        holder.binding.chipConstrainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                if (previousPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousPosition);
                }
                notifyItemChanged(selectedPosition);

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(chipItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chipItemArrayList.size();
    }

    public ChipItem getSelectedItem() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return chipItemArrayList.get(selectedPosition);
        }
        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(ChipItem chipItem);
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        private final ViewholderChipLayoutBinding binding;

        public Viewholder(@NonNull ViewholderChipLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}