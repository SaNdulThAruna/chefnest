package com.sandul.chefnest.ui.admin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.activity.SignInActivity;
import com.sandul.chefnest.ui.admin.fragment.AdminDashboardFragment;
import com.sandul.chefnest.ui.admin.fragment.CuisineManagementFragment;
import com.sandul.chefnest.ui.admin.fragment.DietaryFragment;
import com.sandul.chefnest.ui.admin.fragment.SellerManagementFragment;
import com.sandul.chefnest.ui.admin.fragment.UserManagementFragment;
import com.sandul.chefnest.util.SQLiteHelper;

import java.io.IOException;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    private boolean isVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_drawer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView adminNameTextView = headerView.findViewById(R.id.admin_name);
        ImageView adminImage = headerView.findViewById(R.id.admin_dp);
        loadDP(adminImage);

        ImageView admin_logout = headerView.findViewById(R.id.admin_logout);
        admin_logout.setOnClickListener(v -> {
            new Thread(() -> {
                SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String email = sharedPreferences.getString("email", "");
                if (email != null && !email.isEmpty()) {
                    try (SQLiteHelper sqLiteHelper = new SQLiteHelper(AdminActivity.this, "chefnest.db", null, 1);
                         SQLiteDatabase db = sqLiteHelper.getWritableDatabase()) {
                        db.execSQL("DELETE FROM user WHERE email = ?", new String[]{email});
                        Log.i("UserProfileFragment", "Deleted user with email: " + email);
                    } catch (Exception e) {
                        Log.e("UserProfileFragment", "Error deleting user from database", e);
                    }

                    editor.remove("email");
                    editor.remove("username");
                    editor.remove("userType");
                    editor.remove("account_status");
                    editor.apply();

                    runOnUiThread(() -> {
                        startActivity(new Intent(AdminActivity.this, SignInActivity.class));
                        finish();
                    });
                } else {
                    Log.e("UserProfileFragment", "Email not found in SharedPreferences");
                }
            }).start();
        });

        SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
        String adminName = sharedPreferences.getString("username", "Admin");
        adminNameTextView.setText(adminName);


        ImageView menu = findViewById(R.id.menu);
        DrawerLayout drawer = findViewById(R.id.admin_drawer);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVisible) {
                    drawer.open();
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.admin_dashboard) {
                    openFragment(new AdminDashboardFragment(), drawer);
                } else if (item.getItemId() == R.id.admin_user) {
                    openFragment(new UserManagementFragment(), drawer);
                } else if (item.getItemId() == R.id.admin_seller) {
                    openFragment(new SellerManagementFragment(), drawer);
                } else if (item.getItemId() == R.id.admin_cuisine) {
                    openFragment(new CuisineManagementFragment(), drawer);
                } else if (item.getItemId() == R.id.admin_dietary) {
                    openFragment(new DietaryFragment(), drawer);
                }

                return true;
            }
        });




    }

    private void loadDP(ImageView admin_dp) {
        new Thread(()->{
            try {

                SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "");

                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-dp", "{email:" + email + "}");
                if (jsonObject.get("message").getAsString().equals("success")) {
                    String dpUrl = jsonObject.has("profileImg") ? jsonObject.get("profileImg").getAsString() : "";
                    runOnUiThread(() -> {
                        Glide.with(AdminActivity.this)
                                .load(dpUrl.isEmpty() ? R.drawable.back2 : dpUrl)
                                .into(admin_dp);

                    });
                }
            } catch (IOException e) {
                Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
            }
        }).start();
    }

    private void openFragment(Fragment fragment, DrawerLayout drawerLayout) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_containerView, fragment)
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit();

        drawerLayout.closeDrawers();
    }
}