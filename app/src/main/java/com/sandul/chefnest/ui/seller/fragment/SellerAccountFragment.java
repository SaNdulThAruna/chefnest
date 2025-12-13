package com.sandul.chefnest.ui.seller.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.activity.SignInActivity;
import com.sandul.chefnest.ui.customer.activity.AboutUsActivity;
import com.sandul.chefnest.ui.seller.activity.SellerEditProfileActivity;
import com.sandul.chefnest.util.SQLiteHelper;

import java.io.IOException;
import java.util.Objects;


public class SellerAccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView verify_btn = view.findViewById(R.id.verify_txt);
        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(getActivity().findViewById(R.id.fragmentContainerView).getId(), new SellerVerificationFragment())
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        ConstraintLayout aboutUs = getView().findViewById(R.id.seller_about_us_btn);
        aboutUs.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutUsActivity.class)));


        TextView seller_name = getView().findViewById(R.id.seller_name);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        int accountStatus = sharedPreferences.getInt("account_status", 0);

        seller_name.setText(username);

        loadDP();

        ConstraintLayout unVerify_layout = getView().findViewById(R.id.unVerify_layout);
        ConstraintLayout verify_layout = getView().findViewById(R.id.verify_layout);

        if (accountStatus != 1) {
            unVerify_layout.setVisibility(View.VISIBLE);
            verify_layout.setVisibility(View.INVISIBLE);
        } else {
            unVerify_layout.setVisibility(View.GONE);
            verify_layout.setVisibility(View.VISIBLE);
        }

        LinearLayout sellerOrder = getView().findViewById(R.id.seller_orders);
        LinearLayout sellerPickup = getView().findViewById(R.id.seller_pickup);
        LinearLayout sellerHistory = getView().findViewById(R.id.seller_history);

        BottomNavigationView navigationView = getActivity().findViewById(R.id.bottomNavigationView);

        sellerOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getActivity().findViewById(R.id.fragmentContainerView).getId(), new SellerOrderFragment())
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();

                navigationView.getMenu().findItem(R.id.orders).setChecked(true);
            }
        });


        sellerPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SellerOrderFragment fragment = new SellerOrderFragment();
                Bundle args = new Bundle();
                args.putInt("statusId", 4); // 4 for "Ready for Pickup"
                fragment.setArguments(args);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getActivity().findViewById(R.id.fragmentContainerView).getId(), fragment)
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();

                navigationView.getMenu().findItem(R.id.orders).setChecked(true);
            }
        });


        sellerHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SellerOrderFragment fragment = new SellerOrderFragment();
                Bundle args = new Bundle();
                args.putInt("statusId", 6);
                fragment.setArguments(args);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getActivity().findViewById(R.id.fragmentContainerView).getId(), fragment)
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();

                navigationView.getMenu().findItem(R.id.orders).setChecked(true);

            }
        });


        ImageView editProfile = getView().findViewById(R.id.seller_edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SellerEditProfileActivity.class));
            }
        });


        ConstraintLayout logout = getView().findViewById(R.id.logoutLayout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(() -> {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    String email = sharedPreferences.getString("email", "");
                    if (email != null && !email.isEmpty()) {
                        try (SQLiteHelper sqLiteHelper = new SQLiteHelper(getContext(), "chefnest.db", null, 1);
                             SQLiteDatabase db = sqLiteHelper.getWritableDatabase()) {
                            db.execSQL("DELETE FROM user WHERE email = ?", new String[]{email});
                            Log.i("UserProfileFragment", "Deleted user with email: " + email);
                        } catch (Exception e) {
                            Log.e("UserProfileFragment", "Error deleting user from database", e);
                        }

                        editor.remove("email");
                        editor.remove("userType");
                        editor.apply();

                        getActivity().runOnUiThread(() -> {
                            startActivity(new Intent(getContext(), SignInActivity.class));
                            getActivity().finish();
                        });
                    } else {
                        Log.e("UserProfileFragment", "Email not found in SharedPreferences");
                    }
                }).start();

            }
        });
    }

    private void loadDP(){
        new Thread(()->{
            try {

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "");

                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-dp", "{email:" + email + "}");
                if (jsonObject.get("message").getAsString().equals("success")) {
                    String dpUrl = jsonObject.has("profileImg") ? jsonObject.get("profileImg").getAsString() : "";
                    getActivity().runOnUiThread(() -> {
                        ImageView seller_dp = requireView().findViewById(R.id.seller_dp);
                        Glide.with(requireView())
                                .load(dpUrl.isEmpty() ? R.drawable.back2 : dpUrl)
                                .apply(new RequestOptions().circleCrop())
                                .into(seller_dp);

                    });
                }
            } catch (IOException e) {
                Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
            }
        }).start();
    }
}