package com.sandul.chefnest.ui.seller.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.Product;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.ProductAdapter;
import com.sandul.chefnest.ui.seller.activity.AddProductActivity;

import java.io.IOException;
import java.util.ArrayList;


public class SellerProductFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadChefDishes("");


        EditText search = getView().findViewById(R.id.search_text);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadChefDishes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getView().findViewById(R.id.addProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add Product
                startActivity(new Intent(getContext(), AddProductActivity.class));
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        loadChefDishes("");
    }

    private void loadChefDishes(String searchText) {

        new Thread(() -> {

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try {

                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-chef-dishes", "{\"email\":\"" + email + "\",\"searchText\":\"" + searchText + "\"}");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    ArrayList<Product> productArrayList = new ArrayList<>();

                    jsonObject.get("dishes").getAsJsonArray().forEach(dish -> {

                        JsonObject dishObject = dish.getAsJsonObject();

                        productArrayList.add(
                                new Product(
                                        dishObject.get("id").getAsInt(),
                                        dishObject.get("title").getAsString(),
                                        dishObject.get("price").getAsString(),
                                        dishObject.get("image1Url").getAsString()
                                )
                        );

                    });
                    getActivity().runOnUiThread(() -> {
                        RecyclerView productRecyclerView = getView().findViewById(R.id.seller_product_recyclerView);
                        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                        productRecyclerView.setAdapter(new ProductAdapter(productArrayList));
                    });

                } else {
                    getActivity().runOnUiThread(() -> {
                        Log.e("SellerProductFragment", "Error loading chef dishes: " + jsonObject.get("message").getAsString());
                    });
                }


            } catch (IOException e) {
                Log.e("SellerProductFragment", "Error loading chef dishes", e);
            }

        }).start();

    }

}