package com.sandul.chefnest.ui.customer.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.RangeSlider;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.ChipItem;
import com.sandul.chefnest.model.CityItem;
import com.sandul.chefnest.model.FoodItem;
import com.sandul.chefnest.model.SpinnerItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.CItyItemAdapter;
import com.sandul.chefnest.ui.adapter.ChipItemAdapter;
import com.sandul.chefnest.ui.adapter.FoodItemAdapter;
import com.sandul.chefnest.ui.adapter.SpinnerItemAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {

    private boolean isFilterViewVisible = false;
    private boolean isSliderChange = false;
    private boolean isEditTextChange = false;

    private List<CityItem> cityItemList = new ArrayList<>();
    private CItyItemAdapter cityItemAdapter;

    private int selectedCityId = 0;
    private int cuisineId = 0;
    private final int portionId = 0;
    private int dietaryId = 0;
    private String search = "";
    private int priceSort = 0;
    private double priceMin;
    private double priceMax;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_constraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ConstraintLayout filter = findViewById(R.id.filter);
        View filterView = findViewById(R.id.filter_view);
        View overlayView = findViewById(R.id.overlay_view);

        filterView.setTranslationY(filterView.getHeight());
        filterView.setClickable(true);

        filter.setOnClickListener(v -> {
            if (isFilterViewVisible) {
                hideFilterView(filterView, overlayView);
            } else {
                showFilterView(filterView, overlayView);
            }
            isFilterViewVisible = !isFilterViewVisible;
        });

        overlayView.setOnClickListener(v -> {
            if (isFilterViewVisible) {
                hideFilterView(filterView, overlayView);
                isFilterViewVisible = false;
            }
        });

        ImageView filterCloseButton = findViewById(R.id.filter_close);
        filterCloseButton.setOnClickListener(v -> {
            hideFilterView(filterView, overlayView);
            isFilterViewVisible = false;
        });

        ConstraintLayout selectedCityLayout = findViewById(R.id.selectedCityLayout);
        ConstraintLayout mainFilterLayout = findViewById(R.id.main_filter_contraint_layout);
        ConstraintLayout cityFilterLayout = findViewById(R.id.city_contraint_layout);
        TextView selectedCityName = findViewById(R.id.selected_city_text);

        selectedCityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFilterLayout.setVisibility(View.GONE);
                cityFilterLayout.setVisibility(View.VISIBLE);
            }
        });

        new Thread(() -> {
            try {
                JsonObject dietaryJsonObject = NetworkUtils.makeGetRequest("/load-dietary");
                if (dietaryJsonObject.get("message").getAsString().equals("success")) {
                    ArrayList<ChipItem> chipItems = new ArrayList<>();
                    dietaryJsonObject.get("dietaries").getAsJsonArray().forEach(dietary -> {
                        chipItems.add(new ChipItem(dietary.getAsJsonObject().get("id").getAsInt(), dietary.getAsJsonObject().get("name").getAsString(), false));
                    });

                    runOnUiThread(() -> {
                        RecyclerView dieteryRecyclerView = findViewById(R.id.dietery_item_recyclerView);
                        GridLayoutManager dieteryGridLayoutManager = new GridLayoutManager(this, 2);
                        dieteryRecyclerView.setLayoutManager(dieteryGridLayoutManager);
                        ChipItemAdapter chipItemAdapter = new ChipItemAdapter(chipItems);
                        dieteryRecyclerView.setAdapter(chipItemAdapter);

                        chipItemAdapter.setOnItemClickListener(chipItem -> {
                            Log.d("ChefNestLog", "Selected Item: " + chipItem.getName());
                            dietaryId = chipItem.getId();
                        });
                    });
                }

                JsonObject cuisineJsonObject = NetworkUtils.makeGetRequest("/load-cuisine");
                if (cuisineJsonObject.get("message").getAsString().equals("success")) {
                    ArrayList<ChipItem> chipItems = new ArrayList<>();
                    cuisineJsonObject.get("cuisines").getAsJsonArray().forEach(cuisine -> {
                        chipItems.add(new ChipItem(cuisine.getAsJsonObject().get("id").getAsInt(), cuisine.getAsJsonObject().get("name").getAsString(), false));
                    });

                    runOnUiThread(() -> {
                        RecyclerView cuisineRecyclerView = findViewById(R.id.cuisine_item_recyclerView);
                        GridLayoutManager cuisineGridLayoutManager = new GridLayoutManager(this, 2);
                        cuisineRecyclerView.setLayoutManager(cuisineGridLayoutManager);
                        ChipItemAdapter chipItemAdapter = new ChipItemAdapter(chipItems);
                        cuisineRecyclerView.setAdapter(chipItemAdapter);

                        chipItemAdapter.setOnItemClickListener(chipItem -> {
                            Log.d("ChefNestLog", "Selected Item: " + chipItem.getName());
                            cuisineId = chipItem.getId();
                        });
                    });
                }
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Log.e("ChefNestLog", "Failed to load dietary and cuisine", e);
                });
            }
        }).start();

        Spinner price_spinner = findViewById(R.id.price_spinner);
        SpinnerItemAdapter priceSpinnerAdapter = new SpinnerItemAdapter(this, R.layout.viewholder_spinner_item, new ArrayList<>() {
            {
                add(new SpinnerItem(0, "Select"));
                add(new SpinnerItem(1, "Low to High"));
                add(new SpinnerItem(2, "High to Low"));
            }
        });

        price_spinner.setAdapter(priceSpinnerAdapter);
        price_spinner.setSelection(0);
        price_spinner.setDropDownVerticalOffset(100);
        price_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priceSort = priceSpinnerAdapter.getItem(position).getId();
                loadDishes(cuisineId, portionId, dietaryId, selectedCityId, search, priceMin, priceMax, priceSort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        EditText searchEditText = findViewById(R.id.textView_search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("ChefNestLog", "onTextChanged: " + s);
                search = s.toString();
                loadDishes(cuisineId, portionId, dietaryId, selectedCityId, search, priceMin, priceMax, priceSort);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        RangeSlider rangeSlider = findViewById(R.id.rangeSlider);
        rangeSlider.setValues(0.0f, 10000.0f);

        EditText minPrice = findViewById(R.id.min_price);
        EditText maxPrice = findViewById(R.id.max_price);

        rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (!isEditTextChange) {
                isSliderChange = true;
                List<Float> sliderValues = slider.getValues();
                minPrice.setText(String.valueOf(sliderValues.get(0)));
                maxPrice.setText(String.valueOf(sliderValues.get(1)));

                priceMax = sliderValues.get(1);
                priceMin = sliderValues.get(0);

                isSliderChange = false;
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isSliderChange) {
                    isEditTextChange = true;
                    String minPriceText = minPrice.getText().toString();
                    String maxPriceText = maxPrice.getText().toString();
                    if (!minPriceText.isEmpty() && !maxPriceText.isEmpty()) {
                        float minValue = Float.parseFloat(minPriceText);
                        float maxValue = Float.parseFloat(maxPriceText);
                        minValue = Math.round(minValue / 100) * 100;
                        maxValue = Math.round(maxValue / 100) * 100;
                        rangeSlider.setValues(minValue, maxValue);
                        priceMax = maxValue;
                        priceMin = minValue;
                    }
                    isEditTextChange = false;
                }
            }
        };

        minPrice.addTextChangedListener(textWatcher);
        maxPrice.addTextChangedListener(textWatcher);

        EditText citySearch = findViewById(R.id.editTextText_city_search);
        RecyclerView loadCIty = findViewById(R.id.city_search_recyclerView);

        new Thread(() -> {
            try {
                JsonObject responseJsonObject = NetworkUtils.makeGetRequest("/load-city");
                if (responseJsonObject.get("message").getAsString().equals("success")) {
                    JsonArray cities = responseJsonObject.get("cities").getAsJsonArray();
                    cities.forEach(city -> {
                        CityItem cityItem = new CityItem(city.getAsJsonObject().get("id").getAsInt(), city.getAsJsonObject().get("name").getAsString());
                        if (!cityItemList.contains(cityItem)) {
                            cityItemList.add(cityItem);
                        }
                    });

                    runOnUiThread(() -> {
                        cityItemAdapter = new CItyItemAdapter(cityItemList);
                        loadCIty.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                        loadCIty.setAdapter(cityItemAdapter);

                        cityItemAdapter.setOnItemClickListener(cityItem -> {
                            selectedCityName.setText(cityItem.getCityName());
                            mainFilterLayout.setVisibility(View.VISIBLE);
                            cityFilterLayout.setVisibility(View.GONE);
                            selectedCityId = cityItem.getId();
                        });

                    });
                } else {
                    Log.d("ChefNestLog", "Failed to load city");
                }
            } catch (IOException e) {
                runOnUiThread(()->{
                    Log.e("ChefNestLog", "Failed to load city",e);
                });
            }
        }).start();

        citySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCities(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        Button applyButton = findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadDishes(cuisineId, portionId, dietaryId, selectedCityId, search, priceMin, priceMax, priceSort);
                hideFilterView(filterView, overlayView);
                isFilterViewVisible = false;
            }
        });

    }

    private void loadDishes(int cuisineId, int portionId, int dietaryId, int cityId, String search, double minPrice, double maxPrice, int priceSort) {
        JsonObject requestJsonObject = new JsonObject();
        requestJsonObject.addProperty("cuisineId", cuisineId);
        requestJsonObject.addProperty("portionId", portionId);
        requestJsonObject.addProperty("dietaryId", dietaryId);
        requestJsonObject.addProperty("cityId", cityId);
        requestJsonObject.addProperty("search", search);
        requestJsonObject.addProperty("minPrice", minPrice);
        requestJsonObject.addProperty("maxPrice", maxPrice);
        requestJsonObject.addProperty("priceSort", priceSort);

        new Thread(() -> {
            try {
                JsonObject responseJsonObject = NetworkUtils.makePostRequest("/load-dishes", requestJsonObject.toString());

                if (responseJsonObject.get("message").getAsString().equals("success")) {
                    ArrayList<FoodItem> foodItems = new ArrayList<>();
                    responseJsonObject.get("dishes").getAsJsonArray().forEach(dish -> {
                        foodItems.add(
                                new FoodItem(
                                        dish.getAsJsonObject().get("id").getAsInt(),
                                        dish.getAsJsonObject().get("title").getAsString(),
                                        dish.getAsJsonObject().get("price").getAsString(),
                                        dish.getAsJsonObject().get("cityName").getAsString(),
                                        dish.getAsJsonObject().get("image1Url").getAsString()
                                )
                        );
                    });

                    runOnUiThread(() -> {
                        RecyclerView searchFoodItemRecyclerView = findViewById(R.id.search_product_recyclerView);
                        FoodItemAdapter foodItemAdapter = (FoodItemAdapter) searchFoodItemRecyclerView.getAdapter();
                        if (foodItemAdapter != null) {
                            foodItemAdapter.updateData(foodItems);
                        } else {
                            foodItemAdapter = new FoodItemAdapter(foodItems);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                            searchFoodItemRecyclerView.setLayoutManager(gridLayoutManager);
                            searchFoodItemRecyclerView.setAdapter(foodItemAdapter);
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        RecyclerView searchFoodItemRecyclerView = findViewById(R.id.search_product_recyclerView);
                        FoodItemAdapter foodItemAdapter = (FoodItemAdapter) searchFoodItemRecyclerView.getAdapter();
                        if (foodItemAdapter != null) {
                            foodItemAdapter.updateData(new ArrayList<>());
                        }
                        Log.d("ChefNestLog", responseJsonObject.get("message").toString());
                    });
                }
            } catch (IOException e) {
               runOnUiThread(()->{
                   Log.e("ChefNestLog", "Failed to load dishes",e);
               });
            }
        }).start();
    }

    private void filterCities(String query) {
        List<CityItem> filteredList = cityItemList.stream()
                .filter(city -> city.getCityName().toLowerCase().contains(query.toLowerCase()))
                .distinct() // Ensure the list is unique
                .collect(Collectors.toList());
        cityItemAdapter.updateList(filteredList);
    }

    private void showFilterView(View filterView, View overlayView) {
        filterView.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(filterView, "translationY", filterView.getHeight(), 0);
        animator.setDuration(300);
        animator.start();
    }

    private void hideFilterView(View filterView, View overlayView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(filterView, "translationY", 0, filterView.getHeight());
        animator.setDuration(300);
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                filterView.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
            }
        });
        animator.start();
    }
}