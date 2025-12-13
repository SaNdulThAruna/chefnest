package com.sandul.chefnest.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaos.view.PinView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.customer.activity.HomeActivity;
import com.sandul.chefnest.ui.seller.activity.SellerActivity;

import java.io.IOException;

public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        PinView pinView = findViewById(R.id.pinview);
        Button verifyButton = findViewById(R.id.verify_btn);

        TextView resend = findViewById(R.id.resend);

        Intent intent = getIntent();
        String activity = intent.getStringExtra("activity");

        if (activity != null && activity.equals("signup")) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // send email
                    try {
                        Gson gson = new Gson();

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("email", intent.getStringExtra("email"));

                        JsonObject responseJsonObject = NetworkUtils.makePostRequest("/resend", gson.toJson(jsonObject));

                        if (responseJsonObject != null && "success".equals(responseJsonObject.get("message").getAsString())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // show success message
                                    Toast.makeText(VerificationActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // show error message
                                    Toast.makeText(VerificationActivity.this, responseJsonObject != null ? responseJsonObject.get("message").getAsString() : "Failed to send OTP", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

        }

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // resend code

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // send email
                        try {
                            Gson gson = new Gson();
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("email", intent.getStringExtra("email"));

                            JsonObject responseJsonObject = NetworkUtils.makePostRequest("/resend", gson.toJson(jsonObject));

                            if (responseJsonObject != null && "success".equals(responseJsonObject.get("message").getAsString())) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // show success message
                                        Toast.makeText(VerificationActivity.this, "OTP sent successfully! Please check your Email", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // show error message
                                        Toast.makeText(VerificationActivity.this, responseJsonObject != null ? responseJsonObject.get("message").getAsString() : "Failed to send OTP", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();

            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        Gson gson = new Gson();

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("email", intent.getStringExtra("email"));
                        jsonObject.addProperty("verificationCode", String.valueOf(pinView.getText()));

                        try {
                            JsonObject responseJsonObject = NetworkUtils.makePostRequest("/verification", gson.toJson(jsonObject));

                            if (responseJsonObject != null && "success".equals(responseJsonObject.get("message").getAsString())) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(VerificationActivity.this, "Account verified successfully", Toast.LENGTH_SHORT).show();

                                        /*if (responseJsonObject.get("userType").getAsInt() == 2) {
                                            startActivity(new Intent(VerificationActivity.this, SellerActivity.class));
                                        } else if (responseJsonObject.get("userType").getAsInt() == 3) {
                                            startActivity(new Intent(VerificationActivity.this, HomeActivity.class));
                                        }*/

                                        startActivity(new Intent(VerificationActivity.this, SignInActivity.class));
                                        finish();

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(VerificationActivity.this, responseJsonObject != null ? responseJsonObject.get("message").getAsString() : "Verification Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).

                        start();

            }
        });

    }
}