package com.sandul.chefnest.ui.seller.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.seller.fragment.SellerAccountFragment;
import com.sandul.chefnest.ui.seller.fragment.SellerDashboardFragment;
import com.sandul.chefnest.ui.seller.fragment.SellerOrderFragment;
import com.sandul.chefnest.ui.seller.fragment.SellerProductFragment;

import java.io.IOException;
import java.util.Objects;

public class SellerActivity extends AppCompatActivity {

    private MenuItem previousMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_drawer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);
        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentContainerView);

        // Initialize previousMenuItem to the currently selected item
        previousMenuItem = navigationView.getMenu().findItem(navigationView.getSelectedItemId());

        checkVerificationStatus();

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
                int accountStatus = sharedPreferences.getInt("account_status", 0);

                if (Objects.requireNonNull(item.getTitle()).toString().equals("Dashboard")) {
                    checkVerificationStatus();
                    // Handle Dashboard
                        getSupportFragmentManager().beginTransaction()
                            .replace(fragmentContainerView.getId(), new SellerDashboardFragment())
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                    previousMenuItem = item;

                } else if (Objects.equals(item.getTitle(), "Orders")) {
                    // Handle Orders

                    if (accountStatus != 1) {
                        // Account not verified
                        Toast.makeText(SellerActivity.this, "Please verify your account to access Orders.", Toast.LENGTH_SHORT).show();

                        getSupportFragmentManager().beginTransaction()
                                .replace(fragmentContainerView.getId(), new SellerAccountFragment())
                                .addToBackStack(null)
                                .setReorderingAllowed(true)
                                .commit();

                        navigationView.setSelectedItemId(previousMenuItem.getItemId());
                        return false;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(fragmentContainerView.getId(), new SellerOrderFragment())
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                    previousMenuItem = item;

                } else if (Objects.equals(item.getTitle(), "Product")) {
                    // Handle Product

                    if (accountStatus != 1) {
                        // Account not verified
                        Toast.makeText(SellerActivity.this, "Please verify your account to access Products.", Toast.LENGTH_SHORT).show();

                        getSupportFragmentManager().beginTransaction()
                                .replace(fragmentContainerView.getId(), new SellerAccountFragment())
                                .addToBackStack(null)
                                .setReorderingAllowed(true)
                                .commit();

                        navigationView.setSelectedItemId(previousMenuItem.getItemId());
                        return false;
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(fragmentContainerView.getId(), new SellerProductFragment())
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                    previousMenuItem = item;

                } else if (Objects.equals(item.getTitle(), "Profile")) {
                    checkVerificationStatus();
                    // Handle Account
                    getSupportFragmentManager().beginTransaction()
                            .replace(fragmentContainerView.getId(), new SellerAccountFragment())
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                    previousMenuItem = item;
                }
                return true;
            }
        });
    }

    private void checkVerificationStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        new Thread(() -> {
            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/check-account-status", "{email:" + email + "}");

                if (jsonObject.get("message").getAsString().equals("success")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("account_status", jsonObject.get("account_status").getAsInt());
                    editor.apply();
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to check account status", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}