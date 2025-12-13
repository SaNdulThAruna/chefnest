package com.sandul.chefnest.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sandul.chefnest.databinding.ViewholderLoadTypeBinding;
import com.sandul.chefnest.databinding.ViewholderUserItemBinding;
import com.sandul.chefnest.model.TypeItem;

import java.util.ArrayList;

public class TypeItemAdapter extends RecyclerView.Adapter<TypeItemAdapter.Viewholder> {

    private ArrayList<TypeItem> typeItemArrayList;

    public TypeItemAdapter(ArrayList<TypeItem> typeItemArrayList) {
        this.typeItemArrayList = typeItemArrayList;
    }

    @NonNull
    @Override
    public TypeItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(ViewholderLoadTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TypeItemAdapter.Viewholder holder, int position) {
        holder.bind(typeItemArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return typeItemArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private ViewholderLoadTypeBinding binding;

        public Viewholder(@NonNull ViewholderLoadTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TypeItem typeItem) {
            binding.itemType.setText(typeItem.getType());
            binding.itemId.setText(String.format("#%s", String.valueOf(typeItem.getId())));
        }
    }
}
