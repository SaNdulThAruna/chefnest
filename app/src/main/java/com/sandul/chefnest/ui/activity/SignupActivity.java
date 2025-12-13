package com.sandul.chefnest.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;

import java.io.IOException;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            v.setPadding(0, 0, 0, 0);
            return insets;
        });

        TextInputEditText first_name = findViewById(R.id.register_first_name);
        TextInputEditText last_name = findViewById(R.id.register_last_name);
        TextInputEditText email = findViewById(R.id.register_email);
        TextInputEditText password = findViewById(R.id.register_password);

        final String[] userType = {""};

        ChipGroup userTypeChipGroup = findViewById(R.id.userType_chipGroup);
        userTypeChipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if (!checkedIds.isEmpty()) {
                    int selectedChipId = checkedIds.get(0);
                    Chip selectedChip = group.findViewById(selectedChipId);

                    if (selectedChip.getText().equals("Customer")) {
                        userType[0] = "3";
                    } else {
                        userType[0] = "2";
                    }
                }
            }
        });

        Button signupButton = findViewById(R.id.signup_btn);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = first_name.getText().toString();
                String lastName = last_name.getText().toString();
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        JsonObject requestBody = new JsonObject();

                        requestBody.addProperty("firstName", firstName);
                        requestBody.addProperty("lastName", lastName);
                        requestBody.addProperty("email", userEmail);
                        requestBody.addProperty("password", userPassword);
                        requestBody.addProperty("userType", userType[0]);

                        try {
                            JsonObject responseJsonObject = NetworkUtils.makePostRequest("/signup", gson.toJson(requestBody));

                            if (responseJsonObject.get("message").getAsString().equals("success")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, VerificationActivity.class);
                                        intent.putExtra("email", userEmail);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignupActivity.this, responseJsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignupActivity.this, "Server error. Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}