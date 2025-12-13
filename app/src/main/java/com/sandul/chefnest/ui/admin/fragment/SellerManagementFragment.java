package com.sandul.chefnest.ui.admin.fragment;

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
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.UserItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.UserAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class SellerManagementFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadUserData();
    }

    private void loadUserData() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            JsonObject body = new JsonObject();
            body.addProperty("email", email);
            body.addProperty("type", 2);

            try {
                JsonObject responseJsonObject = NetworkUtils.makePostRequest("/admin/load-users", body.toString());

                if (responseJsonObject != null && responseJsonObject.get("message").getAsString().equals("success")) {
                    getActivity().runOnUiThread(() -> {
                        ArrayList<UserItem> userItems = new ArrayList<>();
                        responseJsonObject.getAsJsonArray("users").forEach(user -> {
                            JsonObject userObject = user.getAsJsonObject();
                            userItems.add(
                                    new UserItem(
                                            userObject.get("id").getAsInt(),
                                            userObject.get("firstName").getAsString() + " " + userObject.get("lastName").getAsString(),
                                            userObject.get("email").getAsString(),
                                            userObject.get("accountStatus").getAsInt(),
                                            userObject.has("profileImg") ? userObject.get("profileImg").getAsString() : ""
                                    )
                            );
                        });

                        RecyclerView usersRecyclerView = getView().findViewById(R.id.chefs_recyclerView);
                        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                        usersRecyclerView.setAdapter(new UserAdapter(userItems, userItem -> {
                            updateUserStatus(userItem.getId(), userItem.getUserStatus());
                        }));
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), responseJsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void updateUserStatus(int userId, int status) {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            JsonObject body = new JsonObject();
            body.addProperty("email", email);
            body.addProperty("id", userId);
            body.addProperty("status", status == 1 ? 2 : 1);

            try {
                JsonObject responseJsonObject = NetworkUtils.makePostRequest("/admin/update-user-status", body.toString());

                if (responseJsonObject != null && responseJsonObject.get("message").getAsString().equals("success")) {
                    getActivity().runOnUiThread(() -> {
                        loadUserData();
                        Toast.makeText(getActivity(), "Chefs status updated successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), responseJsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

}