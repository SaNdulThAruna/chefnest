package com.sandul.chefnest.ui.customer.activity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.CheckoutItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.CheckoutItemAdapter;
import com.sandul.chefnest.ui.customer.fragment.NotificationFragment;
import com.sandul.chefnest.ui.customer.fragment.OrdersFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutActivity extends AppCompatActivity {
    private static final int PAYHERE_REQUEST = 11001;
    public int addressID = 0;
    private double price;
    private String addressLine1;
    private String addressLine2;
    private String mobile;
    private String postalCode;
    private String email;
    private String city;
    private ArrayList<String> list;
    private final ArrayList<String> imgUrl = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadCheckoutData();

        ImageView checkout_back = findViewById(R.id.checkout_back);
        checkout_back.setOnClickListener(v -> finish());

        ImageView checkout_right = findViewById(R.id.checkout_right);
        checkout_right.setOnClickListener(v -> startActivity(new Intent(CheckoutActivity.this, AddressActivity.class)));

        Button checkoutBtn = findViewById(R.id.checkout);
        checkoutBtn.setOnClickListener(v -> checkout(list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
        addressID = sharedPreferences.getInt("addressId", 0);
        String addressText = sharedPreferences.getString("address", "");
        addressLine1 = sharedPreferences.getString("line1", "");
        addressLine2 = sharedPreferences.getString("line2", "");
        mobile = sharedPreferences.getString("mobile", "");
        postalCode = sharedPreferences.getString("postalCode", "");
        city = sharedPreferences.getString("city", "");
        email = sharedPreferences.getString("email", "");

        if (addressID != 0 && !addressText.isEmpty()) {
            TextView address = findViewById(R.id.checkout_address);
            address.setText(addressText);
            Toast.makeText(this, "Address added successfully: " + addressID, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCheckoutData() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        new Thread(() -> {
            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-checkout", "{email:\"" + email + "\"}");
                if (jsonObject.get("message").getAsString().equals("success")) {
                    runOnUiThread(() -> {
                        if (!jsonObject.get("address").getAsString().equals("empty")) {
                            TextView address = findViewById(R.id.checkout_address);
                            addressID = jsonObject.get("addressId").getAsInt();
                            address.setText(jsonObject.get("address").getAsString());

                            addressLine1 = jsonObject.get("line1").getAsString();
                            addressLine2 = jsonObject.get("line2").getAsString();
                            mobile = jsonObject.get("mobile").getAsString();
                            postalCode = jsonObject.get("postalCode").getAsString();
                            city = jsonObject.get("city").getAsString();

                            RecyclerView checkout_items = findViewById(R.id.checkout_items);
                            ArrayList<CheckoutItem> checkoutItems = new ArrayList<>();
                            list = new ArrayList<>();
                            jsonObject.get("cart").getAsJsonArray().forEach(cartItem -> {
                                checkoutItems.add(new CheckoutItem(
                                        cartItem.getAsJsonObject().get("id").getAsInt(),
                                        cartItem.getAsJsonObject().get("foodTitle").getAsString(),
                                        cartItem.getAsJsonObject().get("quantity").getAsInt(),
                                        cartItem.getAsJsonObject().get("price").getAsDouble(),
                                        cartItem.getAsJsonObject().get("image1").getAsString()
                                ));
                                list.add(cartItem.getAsJsonObject().get("foodTitle").getAsString());
                                imgUrl.add(cartItem.getAsJsonObject().get("image1").getAsString());
                            });

                            checkout_items.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
                            checkout_items.setAdapter(new CheckoutItemAdapter(checkoutItems));

                            TextView subtotal = findViewById(R.id.checkout_subtotal);
                            subtotal.setText(String.valueOf(jsonObject.get("subtotal").getAsDouble()));
                            TextView delivery = findViewById(R.id.checkout_delivery);
                            delivery.setText(String.valueOf(jsonObject.get("delivery").getAsDouble()));
                            TextView total = findViewById(R.id.checkout_total);
                            total.setText(String.valueOf(jsonObject.get("total").getAsDouble()));
                            price = jsonObject.get("total").getAsDouble();
                        } else {
                            Toast.makeText(this, "Please add an address to proceed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void checkout(ArrayList<String> description) {

        String uniqueReferenceID = UUID.randomUUID().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String[] s = username.split(" ");
        String firstName = s[0];
        String lastName = s[1];

        InitRequest req = new InitRequest();
        req.setMerchantId("1223790");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(price);             // Final Amount to be charged
        req.setOrderId(uniqueReferenceID);        // Unique Reference ID
        // Item description title
        description.forEach(req::setItemsDescription);
        req.getCustomer().setFirstName(firstName);
        req.getCustomer().setLastName(lastName);
        req.getCustomer().setEmail(email);
        req.getCustomer().setPhone(mobile);
        req.getCustomer().getAddress().setAddress(addressLine1);
        req.getCustomer().getAddress().setCity(city);
        req.getCustomer().getAddress().setCountry("Sri Lanka");

//Optional Params
        /*req.setNotifyUrl(“xxxx”);           // Notifiy Url
        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road");
        req.getCustomer().getDeliveryAddress().setCity("Kadawatha");
        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
        req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));*/

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        startActivityForResult(intent, PAYHERE_REQUEST); //unique request ID e.g. "11001"

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null) {
                    if (response.isSuccess()) {
                        msg = "Activity result:" + response.getData().toString();

                        new Thread(() -> {
                            try {

                                JsonObject req = new JsonObject();
                                req.addProperty("email", email);
                                req.addProperty("addressId", addressID);
                                req.addProperty("total", price);

                                JsonObject jsonObject = NetworkUtils.makePostRequest("/checkout", req.toString());
                                if (jsonObject.get("message").getAsString().equals("success")) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show();

                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                                        HashMap<String, Object> order = new HashMap<>();
                                        imgUrl.forEach(img -> {
                                            order.put("email", email);
                                            order.put("title","Order Placed");
                                            order.put("msg", "Order placed successfully");
                                            order.put("imgUrl", img);
                                            order.put("date", new Date());
                                            order.put("status", 1);
                                        });

                                        firestore.collection("notification")
                                                .add(order)
                                                .addOnSuccessListener(documentReference -> {
                                                    Log.d("FirebaseFirestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w("FirebaseFirestore", "Error adding document", e);
                                                });

                                        NotificationManager notificationManager = getSystemService(NotificationManager.class);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                            NotificationChannel channel = new NotificationChannel("order", "OrderChannel", NotificationManager.IMPORTANCE_DEFAULT);

                                            notificationManager.createNotificationChannel(channel);

                                        }

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(CheckoutActivity.this, "order")
                                                .setContentTitle("Order Placed")
                                                .setContentText("Your order has been placed successfully")
                                                .setSmallIcon(R.drawable.chef)
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                .setAutoCancel(true);

                                        notificationManager.notify(1, builder.build());
                                        finish();
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();

                    } else {
                        msg = "Result:" + response.toString();
                    }
                } else {
                    msg = "Result: no response";
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
//
                    Toast.makeText(this, "User canceled the request", Toast.LENGTH_SHORT).show();

                } else {
//                    textView.setText("User canceled the request");
                    Toast.makeText(this, "User canceled the request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}