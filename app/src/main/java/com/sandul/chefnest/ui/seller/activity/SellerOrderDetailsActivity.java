package com.sandul.chefnest.ui.seller.activity;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.SpinnerItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.SpinnerItemAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SellerOrderDetailsActivity extends AppCompatActivity {

    String imageUrl;
    String statusType;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_order_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_drawer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner = findViewById(R.id.status_spinner);
        //spinner loading
        loadSpinnerStatus(spinner);

        //order details loading
        loadOrderDetails();

        ImageView back = findViewById(R.id.seller_order_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadSpinnerStatus(Spinner spinner) {

        String orderId = getIntent().getStringExtra("orderId");

        new Thread(() -> {

            try {

                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-order-status", "");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    ArrayList<SpinnerItem> spinnerItems = new ArrayList<>();
                    spinnerItems.add(new SpinnerItem(0, "Select Status"));
                    jsonObject.get("status").getAsJsonArray().forEach(jsonElement -> {
                        spinnerItems.add(new SpinnerItem(jsonElement.getAsJsonObject().get("id").getAsInt(), jsonElement.getAsJsonObject().get("name").getAsString()));
                    });

                    runOnUiThread(() -> {
                        SpinnerItemAdapter orderStatusAdapter = new SpinnerItemAdapter(SellerOrderDetailsActivity.this, R.layout.viewholder_spinner_item, spinnerItems);
                        spinner.setAdapter(orderStatusAdapter);
                        spinner.setSelection(0);
                        spinner.setDropDownVerticalOffset(100);
                        spinner.setBackground(null);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                SpinnerItem item = (SpinnerItem) parent.getItemAtPosition(position);

                                if (item.getId() != 0) {
                                    updateOrderStatus(Integer.parseInt(orderId), item.getId());
                                    statusType = item.getName();
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    });

                } else {
                    Log.e("SellerOrderDetailsActivity", "Error loading order status: " + jsonObject.get("message").getAsString());
                }

            } catch (IOException e) {
                Log.e("SellerOrderDetailsActivity", "Error loading order status", e);
            }

        }).start();

    }

    private void loadOrderDetails() {

        String orderId = getIntent().getStringExtra("orderId");

        new Thread(() -> {

            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-user-order-details", "{id:\"" + orderId + "\"}");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    JsonObject orderDetails = jsonObject.get("order").getAsJsonObject();

                    runOnUiThread(() -> {

                        TextView address = findViewById(R.id.seller_order_address);
                        TextView title = findViewById(R.id.seller_order_title);
                        ImageView imageView = findViewById(R.id.seller_order_details_img);
                        TextView price = findViewById(R.id.seller_order_price);
                        TextView qty = findViewById(R.id.seller_order_qty);
                        TextView id = findViewById(R.id.seller_order_id);

                        address.setText(orderDetails.get("address").getAsString());
                        title.setText(orderDetails.get("title").getAsString());
                        Glide.with(this).load(orderDetails.get("image1").getAsString()).into(imageView);
                        imageUrl = orderDetails.get("image1").getAsString();
                        price.setText(String.format("Rs.%s", orderDetails.get("price").getAsString()));
                        qty.setText(orderDetails.get("qty").getAsString());
                        id.setText(String.format("#%s", orderDetails.get("id").getAsString()));
                        userEmail = orderDetails.get("userEmail").getAsString();
                        orderDetails.get("statusList").getAsJsonArray().forEach(status -> {
                            JsonObject statusJson = status.getAsJsonObject();
                            int statusId = statusJson.get("statusId").getAsInt();
                            String date = statusJson.get("date").getAsString();
                            String time = statusJson.get("time").getAsString();

                            ImageView confirmed = findViewById(R.id.seller_order_status_confirmed);
                            TextView confirmedDate = findViewById(R.id.seller_confiremed_date);
                            TextView confirmedTime = findViewById(R.id.seller_confiremed_time);

                            ImageView preparing = findViewById(R.id.seller_order_status_preparing);
                            TextView preparingDate = findViewById(R.id.seller_preparing_date);
                            TextView preparingTime = findViewById(R.id.seller_preparing_time);

                            ImageView pickup = findViewById(R.id.seller_order_status_pickup);
                            TextView pickupDate = findViewById(R.id.seller_pickup_date);
                            TextView pickupTime = findViewById(R.id.seller_pickup_time);

                            ImageView delivery = findViewById(R.id.seller_order_status_delivery);
                            TextView deliveryDate = findViewById(R.id.seller_delivery_date);
                            TextView deliveryTime = findViewById(R.id.seller_delivery_time);

                            ImageView delivered = findViewById(R.id.seller_order_status_delivered);
                            TextView deliveredDate = findViewById(R.id.seller_delivered_date);
                            TextView deliveredTime = findViewById(R.id.seller_delivered_time);

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

                            TextView subTotal = findViewById(R.id.seller_customer_order_summary_sub_total);
                            TextView deliveryCharge = findViewById(R.id.seller_customer_order_summary_delivery);
                            TextView total = findViewById(R.id.seller_customer_order_summary_total);

                            subTotal.setText(String.format("Rs.%s", orderDetails.get("subTotal").getAsString()));
                            deliveryCharge.setText(String.format("Rs.%s", orderDetails.get("delivery").getAsString()));
                            total.setText(String.format("Rs.%s", orderDetails.get("total").getAsString()));
                        });

                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        Log.e("SellerOrderDetailsActivity", jsonObject.get("message").getAsString());
                    });
                }

            } catch (IOException e) {
                Log.e("SellerOrderDetailsActivity", "Error loading order details", e);
            }

        }).start();

    }

    private void updateOrderStatus(int orderId, int statusId) {

        new Thread(() -> {

            SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/update-order-status", "{\"email\":\"" + email + "\",\"id\":\"" + orderId + "\",\"status\":\"" + statusId + "\"}");

                if (jsonObject.get("message").getAsString().equals("success")) {
                    runOnUiThread(() -> {

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        HashMap<String, Object> order = new HashMap<>();
                        order.put("email", email);
                        order.put("title", statusType);
                        order.put("msg", statusType + " successfully");
                        order.put("imgUrl", imageUrl);
                        order.put("date", new Date());
                        order.put("status", 1);

                        db.collection("notification")
                                .add(order)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("FirebaseFirestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirebaseFirestore", "Error adding document", e);
                                });

                        Toast.makeText(this, "Order status updated successfully", Toast.LENGTH_SHORT).show();
                        //refresh order details
                        loadOrderDetails();

                    });
                } else {
                    Log.e("SellerOrderDetailsActivity", "Error updating order status: " + jsonObject.get("message").getAsString());
                }

            } catch (IOException e) {
                Log.e("SellerOrderDetailsActivity", "Error updating order status", e);
            }
        }).start();
    }
}