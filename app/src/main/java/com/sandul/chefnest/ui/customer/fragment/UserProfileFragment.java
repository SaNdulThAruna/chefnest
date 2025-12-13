package com.sandul.chefnest.ui.customer.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.PopularItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.activity.SignInActivity;
import com.sandul.chefnest.ui.adapter.PopularItemAdapter;
import com.sandul.chefnest.ui.customer.activity.AboutUsActivity;
import com.sandul.chefnest.ui.customer.activity.CartActivity;
import com.sandul.chefnest.ui.customer.activity.UserEditProfileActivity;
import com.sandul.chefnest.util.SQLiteHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class UserProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }


    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout toPay= requireView().findViewById(R.id.user_pay);
        toPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CartActivity.class));
            }
        });

        TextView userName = getView().findViewById(R.id.user_name);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        userName.setText(username);

        loadDP();

        ImageView editProfile = getView().findViewById(R.id.user_edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserEditProfileActivity.class));
            }
        });

        ConstraintLayout aboutUs = getView().findViewById(R.id.about_us_btn);

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutUsActivity.class));
            }
        });

        LinearLayout userOrders = getView().findViewById(R.id.user_orders);

        userOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentContainerView navView = getActivity().findViewById(R.id.navigationViewContainerView);
                if (navView != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(navView.getId(), new OrdersFragment(), null)
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                }
            }
        });


        LinearLayout userShipped = getView().findViewById(R.id.user_shipped);
        userShipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentContainerView navView = getActivity().findViewById(R.id.navigationViewContainerView);
                if (navView != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(navView.getId(), new ShippedFragment(), null)
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                }
            }
        });


        LinearLayout userHistory = getView().findViewById(R.id.user_history);
        userHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentContainerView navView = getActivity().findViewById(R.id.navigationViewContainerView);
                if (navView != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(navView.getId(), new OrderHistoryFragment(), null)
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                }
            }
        });


        ConstraintLayout signOutBtn = getView().findViewById(R.id.customer_signOut);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
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

    }*/


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout toPay = requireView().findViewById(R.id.user_pay);
        toPay.setOnClickListener(v -> startActivity(new Intent(getContext(), CartActivity.class)));

        TextView userName = getView().findViewById(R.id.user_name);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        userName.setText(username);

        loadDP();

        ImageView editProfile = getView().findViewById(R.id.user_edit_profile);
        editProfile.setOnClickListener(v -> startActivity(new Intent(getContext(), UserEditProfileActivity.class)));

        ConstraintLayout aboutUs = getView().findViewById(R.id.about_us_btn);
        aboutUs.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutUsActivity.class)));

        LinearLayout userOrders = getView().findViewById(R.id.user_orders);
        userOrders.setOnClickListener(v -> {
            FragmentContainerView navView = getActivity().findViewById(R.id.navigationViewContainerView);
            if (navView != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(navView.getId(), new OrdersFragment(), null)
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        LinearLayout userShipped = getView().findViewById(R.id.user_shipped);
        userShipped.setOnClickListener(v -> {
            FragmentContainerView navView = getActivity().findViewById(R.id.navigationViewContainerView);
            if (navView != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(navView.getId(), new ShippedFragment(), null)
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        LinearLayout userHistory = getView().findViewById(R.id.user_history);
        userHistory.setOnClickListener(v -> {
            FragmentContainerView navView = getActivity().findViewById(R.id.navigationViewContainerView);
            if (navView != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(navView.getId(), new OrderHistoryFragment(), null)
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        LinearLayout userReviews = getView().findViewById(R.id.user_reviews);
        userReviews.setOnClickListener(v -> {
            FragmentContainerView navView = getActivity().findViewById(R.id.navigationViewContainerView);
            if (navView != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(navView.getId(), new ToRewiewsFragment(), null)
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        ConstraintLayout signOutBtn = getView().findViewById(R.id.customer_signOut);
        signOutBtn.setOnClickListener(v -> new Thread(() -> {
            SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("com.sandul.chefnest.data", getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();

            String email = sharedPreferences1.getString("email", "");
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
        }).start());
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

                        if (getView() == null) return;

                        ImageView seller_dp = getView().findViewById(R.id.user_dp);
                        Glide.with(getContext())
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