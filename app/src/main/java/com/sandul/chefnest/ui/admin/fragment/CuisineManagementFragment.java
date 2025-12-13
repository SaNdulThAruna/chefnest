package com.sandul.chefnest.ui.admin.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.TypeItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.TypeItemAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class CuisineManagementFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cuisine_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        loadCuisineData();

        Button cuisine_button = getView().findViewById(R.id.cuisine_button);
        cuisine_button.setOnClickListener(v -> {
            EditText cuisine_name = getView().findViewById(R.id.editText_cuisine);
            addCuisine(cuisine_name.getText().toString());
        });

    }

    private void loadCuisineData() {
        new Thread(() -> {
            try {
                JsonObject responseJsonObject = NetworkUtils.makeGetRequest("/load-cuisine");

                if (responseJsonObject != null && responseJsonObject.get("message").getAsString().equals("success")) {
                    getActivity().runOnUiThread(() -> {


                        ArrayList<TypeItem> typeItems = new ArrayList<>();
                        responseJsonObject.getAsJsonArray("cuisines").forEach(type -> {
                            JsonObject typeObject = type.getAsJsonObject();
                            typeItems.add(
                                    new TypeItem(
                                            typeObject.get("id").getAsInt(),
                                            typeObject.get("name").getAsString()
                                    )
                            );
                        });
                        RecyclerView recyclerView = getView().findViewById(R.id.cuisines_view);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(new TypeItemAdapter(typeItems));

                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load cuisine data", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addCuisine(String cuisineName) {
        new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "");

                JsonObject body = new JsonObject();
                body.addProperty("email", email);
                body.addProperty("cuisineName", cuisineName);
                JsonObject responseJsonObject = NetworkUtils.makePostRequest("/admin/add-cuisine", body.toString());

                if (responseJsonObject != null && responseJsonObject.get("message").getAsString().equals("success")) {
                    getActivity().runOnUiThread(() -> {
                        loadCuisineData();
                        EditText cuisine_name = getView().findViewById(R.id.editText_cuisine);
                        cuisine_name.setText("");
                        Toast.makeText(getContext(), "Cuisine added successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to add cuisine", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}