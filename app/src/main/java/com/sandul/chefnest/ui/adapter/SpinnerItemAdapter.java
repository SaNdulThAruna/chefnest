package com.sandul.chefnest.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sandul.chefnest.databinding.ViewholderSpinnerBinding;
import com.sandul.chefnest.databinding.ViewholderSpinnerItemBinding;
import com.sandul.chefnest.model.SpinnerItem;

import java.util.List;

public class SpinnerItemAdapter extends ArrayAdapter<SpinnerItem> {

    private final List<SpinnerItem> spinnerItems;
    private final LayoutInflater inflater;

    public SpinnerItemAdapter(@NonNull Context context, int resource, List<SpinnerItem> spinnerItems) {
        super(context, resource, spinnerItems);
        this.spinnerItems = spinnerItems;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewholderSpinnerItemBinding binding;
        if (convertView == null) {
            binding = ViewholderSpinnerItemBinding.inflate(inflater, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ViewholderSpinnerItemBinding) convertView.getTag();
        }
        binding.spinnerItemName.setText(spinnerItems.get(position).getName());
        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewholderSpinnerBinding binding;
        if (convertView == null) {
            binding = ViewholderSpinnerBinding.inflate(inflater, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ViewholderSpinnerBinding) convertView.getTag();
        }
        binding.spinnerText.setText(spinnerItems.get(position).getName());
        return convertView;
    }
}