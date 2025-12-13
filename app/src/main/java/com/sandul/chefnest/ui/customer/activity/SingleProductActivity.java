package com.sandul.chefnest.ui.customer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.ImagePagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        new Thread(() -> {

            try {

                JsonObject jsonObject = NetworkUtils.makePostRequest("/single-product", "{id:" + id + "}");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    runOnUiThread(() -> {

                        ImageView imageView = findViewById(R.id.single_product_img);
                        ViewPager2 imagePager = findViewById(R.id.single_product_imgView);
                        TextView title = findViewById(R.id.single_product_title);
                        TextView price = findViewById(R.id.single_product_price);
                        TextView portion = findViewById(R.id.single_product_portion_size);
                        TextView description = findViewById(R.id.single_product_des);

                        final int[] qty = {jsonObject.get("qty").getAsInt()};

                        title.setText(jsonObject.get("title").getAsString());
                        price.setText(String.format("Rs.%s", jsonObject.get("price").getAsString()));
                        portion.setText(jsonObject.get("portion").getAsString());
                        description.setText(jsonObject.get("description").getAsString());

                        List<String> imageUrls = new ArrayList<>();
                        imageUrls.add(jsonObject.get("img1").getAsString());
                        imageUrls.add(jsonObject.get("img2").getAsString());

                        ImagePagerAdapter adapter = new ImagePagerAdapter(imageUrls);
                        imagePager.setAdapter(adapter);

                        ImageView plus = findViewById(R.id.single_product_plus);
                        ImageView minus = findViewById(R.id.single_product_minus);
                        TextView qtyText = findViewById(R.id.single_product_qty);

                        qtyText.setText("1");

                        plus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int currentQty = Integer.parseInt(qtyText.getText().toString());
                                if (currentQty < qty[0]) {
                                    qtyText.setText(String.valueOf(currentQty + 1));
                                } else {
                                    Toast.makeText(SingleProductActivity.this, "You can't order more than available quantity", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        minus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int currentQty = Integer.parseInt(qtyText.getText().toString());
                                if (currentQty > 1) {
                                    qtyText.setText(String.valueOf(currentQty - 1));
                                } else {
                                    Toast.makeText(SingleProductActivity.this, "You can't order less than 1", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SingleProductActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_LONG).show();
                    });
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).start();


        Button addToCart = findViewById(R.id.single_product_btn);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int qty = Integer.parseInt(((TextView) findViewById(R.id.single_product_qty)).getText().toString());

                SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "");

                Log.d("ChefNestLog", email);
                Log.d("ChefNestLog", String.valueOf(qty));
                Log.d("ChefNestLog", String.valueOf(id));

                new Thread(() -> {

                    try {
                        JsonObject jsonObject = NetworkUtils.makePostRequest("/add-to-cart", "{email:\"" + email + "\", qty:" + qty + ", foodId:" + id + "}");
                        if (jsonObject.get("message").getAsString().equals("success")) {
                            runOnUiThread(() -> {
                                Toast.makeText(SingleProductActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(SingleProductActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_LONG).show();
                            });
                        }
                    } catch (IOException e) {

                        Log.e("ChefNestLog", e.getMessage());

                    }

                }).start();

            }
        });


        ImageView backButton = findViewById(R.id.product_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}