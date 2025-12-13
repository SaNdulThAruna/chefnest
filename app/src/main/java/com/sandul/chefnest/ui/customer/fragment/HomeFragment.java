package com.sandul.chefnest.ui.customer.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.ChipItem;
import com.sandul.chefnest.model.FoodItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.ChipItemAdapter;
import com.sandul.chefnest.ui.adapter.FoodItemAdapter;
import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private View rootView;
    private int selectedPosition = 0;
    private String search = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText home_search = rootView.findViewById(R.id.home_search);
        home_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search = s.toString();
                loadDishes(selectedPosition, search);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadDietaries();
        loadDishes(selectedPosition, search);
    }

    private void loadDietaries() {
        new Thread(() -> {
            try {
                JsonObject jsonObject = NetworkUtils.makeGetRequest("/load-dietary");

                if (jsonObject.get("message").getAsString().equals("success")) {
                    JsonArray dietaries = jsonObject.get("dietaries").getAsJsonArray();
                    ArrayList<ChipItem> dietaryItemsList = new ArrayList<>();
                    dietaries.forEach(dietary -> {
                        JsonObject dietaryObject = dietary.getAsJsonObject();
                        dietaryItemsList.add(
                                new ChipItem(
                                        dietaryObject.get("id").getAsInt(),
                                        dietaryObject.get("name").getAsString(),
                                        false
                                )
                        );
                    });

                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            RecyclerView dieteryRecyclerView = rootView.findViewById(R.id.dietaryRecyclerView);
                            LinearLayoutManager dietaryLayoutManager = new LinearLayoutManager(getContext());
                            dietaryLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                            dieteryRecyclerView.setLayoutManager(dietaryLayoutManager);

                            ChipItemAdapter chipItemAdapter = new ChipItemAdapter(dietaryItemsList);
                            dieteryRecyclerView.setAdapter(chipItemAdapter);

                            chipItemAdapter.setOnItemClickListener(chipItem -> {
                                chipItem.setSelected(!chipItem.isSelected());
                                selectedPosition = chipItem.getId();
                                loadDishes(selectedPosition, search);
                            });
                        });
                    }
                } else {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } catch (IOException e) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Error loading dietaries", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void loadDishes(int selectedPosition, String search) {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                JsonObject requestJsonObject = new JsonObject();
                requestJsonObject.addProperty("cuisineId", "0");
                requestJsonObject.addProperty("portionId", "0");
                requestJsonObject.addProperty("dietaryId", String.valueOf(selectedPosition));
                requestJsonObject.addProperty("cityId", "0");
                requestJsonObject.addProperty("search", search);
                requestJsonObject.addProperty("minPrice", "0");
                requestJsonObject.addProperty("maxPrice", "0");
                requestJsonObject.addProperty("priceSort", "0");

                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-dishes", gson.toJson(requestJsonObject));

                if (jsonObject.get("message").getAsString().equals("success")) {
                    JsonArray dishes = jsonObject.get("dishes").getAsJsonArray();
                    ArrayList<FoodItem> foodItemsList = new ArrayList<>();
                    dishes.forEach(dish -> {
                        JsonObject dishObject = dish.getAsJsonObject();
                        foodItemsList.add(
                                new FoodItem(
                                        dishObject.get("id").getAsInt(),
                                        dishObject.get("title").getAsString(),
                                        "Rs. " + dishObject.get("price").getAsString(),
                                        dishObject.get("cityName").getAsString(),
                                        dishObject.get("image1Url").getAsString()
                                )
                        );
                    });

                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            RecyclerView foodItemRecyclerView = rootView.findViewById(R.id.foodItemRecyclerView);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                            foodItemRecyclerView.setLayoutManager(gridLayoutManager);
                            foodItemRecyclerView.setAdapter(new FoodItemAdapter(foodItemsList));
                        });
                    }
                } else {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getActivity(), jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } catch (IOException e) {
                Log.e("ChefNestLog", "Error loading dishes", e);
            }
        }).start();
    }
}