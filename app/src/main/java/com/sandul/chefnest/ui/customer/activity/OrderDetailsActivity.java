package com.sandul.chefnest.ui.customer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;

import java.io.IOException;
import java.util.Objects;

public class OrderDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_drawer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the order ID from the intent
        int orderId = getIntent().getIntExtra("order_id", 0);

        new Thread(()->{

            try {

                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-user-order-details", "{id:\"" + orderId + "\"}");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    JsonObject orderDetails = jsonObject.get("order").getAsJsonObject();
                    runOnUiThread(() -> {
                        // Set the order details


                        TextView address = findViewById(R.id.order_address);
                        TextView title = findViewById(R.id.order_title);
                        ImageView imageView = findViewById(R.id.order_details_img);
                        TextView price = findViewById(R.id.order_price);
                        TextView qty = findViewById(R.id.order_qty);
                        TextView id = findViewById(R.id.order_id);

                        address.setText(orderDetails.get("address").getAsString());
                        title.setText(orderDetails.get("title").getAsString());
                        Glide.with(this).load(orderDetails.get("image1").getAsString()).into(imageView);
                        price.setText(String.format("Rs.%s", orderDetails.get("price").getAsString()));
                        qty.setText(orderDetails.get("qty").getAsString());
                        id.setText(String.format("#%s", orderDetails.get("id").getAsString()));

                        orderDetails.get("statusList").getAsJsonArray().forEach(status -> {
                            JsonObject statusJson = status.getAsJsonObject();
                            int statusId = statusJson.get("statusId").getAsInt();
                            String date = statusJson.get("date").getAsString();
                            String time = statusJson.get("time").getAsString();

                            ImageView confirmed = findViewById(R.id.order_status_confirmed);
                            TextView confirmedDate = findViewById(R.id.confiremed_date);
                            TextView confirmedTime = findViewById(R.id.confiremed_time);

                            ImageView preparing = findViewById(R.id.order_status_preparing);
                            TextView preparingDate = findViewById(R.id.preparing_date);
                            TextView preparingTime = findViewById(R.id.preparing_time);

                            ImageView pickup = findViewById(R.id.order_status_pickup);
                            TextView pickupDate = findViewById(R.id.pickup_date);
                            TextView pickupTime = findViewById(R.id.pickup_time);

                            ImageView delivery = findViewById(R.id.order_status_delivery);
                            TextView deliveryDate = findViewById(R.id.delivery_date);
                            TextView deliveryTime = findViewById(R.id.delivery_time);

                            ImageView delivered = findViewById(R.id.order_status_delivered);
                            TextView deliveredDate = findViewById(R.id.delivered_date);
                            TextView deliveredTime = findViewById(R.id.delivered_time);

                            switch (statusId) {
                                case 2:
                                    confirmed.setBackground(getDrawable(R.drawable.order_pending));
                                    confirmedDate.setText(date);
                                    confirmedTime.setText(time);
                                    break;
                                case 3:

                                    confirmed.setBackground(getDrawable(R.drawable.order_pending));
                                    confirmedDate.setText(date);
                                    confirmedTime.setText(time);

                                    preparing.setBackground(getDrawable(R.drawable.order_preparing));
                                    preparingDate.setText(date);
                                    preparingTime.setText(time);
                                    break;
                                case 4:

                                    confirmed.setBackground(getDrawable(R.drawable.order_pending));
                                    confirmedDate.setText(date);
                                    confirmedTime.setText(time);

                                    preparing.setBackground(getDrawable(R.drawable.order_preparing));
                                    preparingDate.setText(date);
                                    preparingTime.setText(time);

                                    pickup.setBackground(getDrawable(R.drawable.order_pickup));
                                    pickupDate.setText(date);
                                    pickupTime.setText(time);
                                    break;
                                case 5:

                                    confirmed.setBackground(getDrawable(R.drawable.order_pending));
                                    confirmedDate.setText(date);
                                    confirmedTime.setText(time);

                                    preparing.setBackground(getDrawable(R.drawable.order_preparing));
                                    preparingDate.setText(date);
                                    preparingTime.setText(time);

                                    pickup.setBackground(getDrawable(R.drawable.order_pickup));
                                    pickupDate.setText(date);
                                    pickupTime.setText(time);

                                    delivery.setBackground(getDrawable(R.drawable.order_delivery));
                                    deliveryDate.setText(date);
                                    deliveryTime.setText(time);
                                    break;
                                case 6:

                                    confirmed.setBackground(getDrawable(R.drawable.order_pending));
                                    confirmedDate.setText(date);
                                    confirmedTime.setText(time);

                                    preparing.setBackground(getDrawable(R.drawable.order_preparing));
                                    preparingDate.setText(date);
                                    preparingTime.setText(time);

                                    pickup.setBackground(getDrawable(R.drawable.order_pickup));
                                    pickupDate.setText(date);
                                    pickupTime.setText(time);

                                    delivery.setBackground(getDrawable(R.drawable.order_delivery));
                                    deliveryDate.setText(date);
                                    deliveryTime.setText(time);

                                    delivered.setBackground(getDrawable(R.drawable.order_delivered));
                                    deliveredDate.setText(date);
                                    deliveredTime.setText(time);
                                    break;
                            }
                        });

                        /*ImageView confirmed = findViewById(R.id.order_status_confirmed);
                        TextView confirmedDate = findViewById(R.id.confiremed_date);
                        TextView confirmedTime = findViewById(R.id.confiremed_time);

                        ImageView preparing = findViewById(R.id.order_status_preparing);
                        TextView preparingDate = findViewById(R.id.preparing_date);
                        TextView preparingTime = findViewById(R.id.preparing_time);

                        ImageView pickup = findViewById(R.id.order_status_pickup);
                        TextView pickupDate = findViewById(R.id.pickup_date);
                        TextView pickupTime = findViewById(R.id.pickup_time);

                        ImageView delivery = findViewById(R.id.order_status_delivery);
                        TextView deliveryDate = findViewById(R.id.delivery_date);
                        TextView deliveryTime = findViewById(R.id.delivery_time);

                        ImageView delivered = findViewById(R.id.order_status_delivered);
                        TextView deliveredDate = findViewById(R.id.delivered_date);
                        TextView deliveredTime = findViewById(R.id.delivered_time);*/

                        TextView subTotal = findViewById(R.id.customer_order_summary_sub_total);
                        TextView deliveryCharge = findViewById(R.id.customer_order_summary_delivery);
                        TextView total = findViewById(R.id.customer_order_summary_total);

                        subTotal.setText(String.format("Rs.%s", orderDetails.get("subTotal").getAsString()));
                        deliveryCharge.setText(String.format("Rs.%s", orderDetails.get("delivery").getAsString()));
                        total.setText(String.format("Rs.%s", orderDetails.get("total").getAsString()));

                    });
                }else{
                    runOnUiThread(() -> {
                        Log.e("ChefNestLog", jsonObject.get("message").getAsString());
                    });
                }

            } catch (IOException e) {
                Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
            }

        }).start();

        ImageView backBtn = findViewById(R.id.order_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}