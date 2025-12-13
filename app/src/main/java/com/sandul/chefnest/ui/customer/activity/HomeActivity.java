package com.sandul.chefnest.ui.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.customer.fragment.HomeFragment;
import com.sandul.chefnest.ui.customer.fragment.NotificationFragment;
import com.sandul.chefnest.ui.customer.fragment.UserProfileFragment;

import java.io.IOException;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        navigationView = findViewById(R.id.customer_bottomNavigationView);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.nav_home) {
                    FragmentContainerView navView = findViewById(R.id.navigationViewContainerView);
                    if (navView != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(navView.getId(), new HomeFragment())
                                .addToBackStack(null)
                                .setReorderingAllowed(true)
                                .commit();
                    }
                } else if (item.getItemId() == R.id.nav_search) {
                    Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_cart) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_notifications) {
                    FragmentContainerView navView = findViewById(R.id.navigationViewContainerView);
                    if (navView != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(navView.getId(), new NotificationFragment())
                                .addToBackStack(null)
                                .setReorderingAllowed(true)
                                .commit();
                    }

                    // Update notification status and remove badge
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                    String email = sharedPreferences.getString("email", "");

                    db.collection("notification")
                            .whereEqualTo("email", email)
                            .whereEqualTo("status", 1)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    task.getResult().forEach(document -> {
                                        db.collection("notification").document(document.getId())
                                                .update("status", 2);
                                    });

                                    BadgeDrawable badge = navigationView.getBadge(R.id.nav_notifications);
                                    if (badge != null) {
                                        badge.setVisible(false);
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirebaseFirestore", "Error updating documents.", e);
                            });

                } else if (item.getItemId() == R.id.nav_profile) {
                    FragmentContainerView navView = findViewById(R.id.navigationViewContainerView);
                    if (navView != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(navView.getId(), new UserProfileFragment())
                                .addToBackStack(null)
                                .setReorderingAllowed(true)
                                .commit();
                    }
                }

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentContainerView navView = findViewById(R.id.navigationViewContainerView);
        if (navView != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(navView.getId(), new HomeFragment())
                    .setReorderingAllowed(true)
                    .commit();
        }
        navigationView.setSelectedItemId(R.id.nav_home);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notification")
                .where(
                        Filter.and(
                                Filter.equalTo("email", Objects.requireNonNull(getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE).getString("email", ""))),
                                Filter.equalTo("status", 1)
                        ))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            int notificationCount = task.getResult().size();
                            BadgeDrawable badge = navigationView.getOrCreateBadge(R.id.nav_notifications);
                            badge.setBackgroundColor(getColor(R.color.orange_1));
                            badge.setBadgeTextColor(getColor(R.color.white));
                            badge.setVisible(notificationCount > 0);
                            badge.setNumber(notificationCount);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseFirestore", "Error getting documents.", e);
                });


        new Thread(() -> {

            SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-cart-qty", "{email:\"" + email + "\"}");


                if (jsonObject.get("message").getAsString().equals("success")) {
                    runOnUiThread(() -> {

                        int cartQty = jsonObject.get("cartQty").getAsInt();

                        BadgeDrawable badge = navigationView.getOrCreateBadge(R.id.nav_cart);
                        badge.setBackgroundColor(getColor(R.color.orange_1));
                        badge.setBadgeTextColor(getColor(R.color.white));
                        badge.setVisible(cartQty > 0);
                        badge.setNumber(cartQty);

//                        navigationView.getOrCreateBadge(R.id.nav_notifications).setVisible(true);
                    });
                } else {
                    runOnUiThread(() -> {
                        Log.e("ChefNestLog", jsonObject.get("message").getAsString());
                    });
                }

            } catch (IOException e) {
                Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
            }

        }).start();

    }
}