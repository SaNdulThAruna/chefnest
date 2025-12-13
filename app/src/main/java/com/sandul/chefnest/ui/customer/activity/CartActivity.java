package com.sandul.chefnest.ui.customer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.CartItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.CartItemAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView cartItemRecyclerView = findViewById(R.id.cart_item_recyclerView);

        new Thread(() -> {

            SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-cart", "{email:\"" + email + "\"}");

                if (jsonObject.get("message").getAsString().equals("success")) {
                    runOnUiThread(() -> {

                        Button checkoutButton = findViewById(R.id.cart_button);
                        checkoutButton.setEnabled(true);

                        ArrayList<CartItem> cartItemsList = new ArrayList<>();
                        jsonObject.get("cartList").getAsJsonArray().forEach(cartItem -> {
                            cartItemsList.add(
                                    new CartItem(
                                            cartItem.getAsJsonObject().get("id").getAsInt(),
                                            cartItem.getAsJsonObject().get("foodTitle").getAsString(),
                                            cartItem.getAsJsonObject().get("price").getAsString(),
                                            cartItem.getAsJsonObject().get("qty").getAsString(),
                                            cartItem.getAsJsonObject().get("image1").getAsString()
                                    )
                            );

                            checkoutButton.setOnClickListener(V->{
                                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
                                finish();
                            });
                        });


                        double subtotal = jsonObject.get("subtotal").getAsDouble();
                        TextView subTotalView = findViewById(R.id.cart_subTotal);
                        subTotalView.setText(String.valueOf(subtotal));

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        cartItemRecyclerView.setLayoutManager(linearLayoutManager);
                        cartItemRecyclerView.setAdapter(new CartItemAdapter(this, cartItemsList, subTotalView,subtotal));



                    });
                } else if (jsonObject.get("message").getAsString().equals("empty")) {
                    runOnUiThread(() -> {
                        Button checkoutButton = findViewById(R.id.cart_button);
                        checkoutButton.setEnabled(false);
                        Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(CartActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_LONG).show();
                    });
                }

            } catch (IOException e) {
                Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
            }

        }).start();


        ImageView backButton = findViewById(R.id.cart_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}