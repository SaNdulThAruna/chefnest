package com.sandul.chefnest.ui.customer.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.AddressItem;
import com.sandul.chefnest.model.SpinnerItem;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.AddressItemAdapter;
import com.sandul.chefnest.ui.adapter.SpinnerItemAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity {

    private int cityId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadAddressData();
        loadCities();

        findViewById(R.id.add_btn).setOnClickListener(v -> addAddress());
    }

    private void loadCities() {
        new Thread(() -> {
            try {
                JsonObject jsonObject = NetworkUtils.makeGetRequest("/load-city");
                if (jsonObject.get("message").getAsString().equals("success")) {
                    runOnUiThread(() -> {
                        Spinner citySpinner = findViewById(R.id.ad_spinner);
                        ArrayList<SpinnerItem> cities = new ArrayList<>();
                        cities.add(new SpinnerItem(0, "Select City"));
                        jsonObject.get("cities").getAsJsonArray().forEach(city -> {
                            cities.add(new SpinnerItem(city.getAsJsonObject().get("id").getAsInt(), city.getAsJsonObject().get("name").getAsString()));
                        });

                        SpinnerItemAdapter priceSpinnerAdapter = new SpinnerItemAdapter(this, R.layout.viewholder_spinner, cities);
                        citySpinner.setAdapter(priceSpinnerAdapter);
                        citySpinner.setSelection(0);
                        citySpinner.setDropDownVerticalOffset(100);

                        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                cityId = parent.getSelectedItemPosition();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addAddress() {
        EditText ad1 = findViewById(R.id.ad1);
        EditText ad2 = findViewById(R.id.ad2);
        EditText ad_mobile = findViewById(R.id.ad_mobile);
        EditText ad_code = findViewById(R.id.ad_code);

        String addressLine1 = ad1.getText().toString();
        String addressLine2 = ad2.getText().toString();
        String mobile = ad_mobile.getText().toString();
        String postalCode = ad_code.getText().toString();
        String email = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE).getString("email", "");
        String city = String.valueOf(cityId);

        new Thread(() -> {
            Gson gson = new Gson();
            JsonObject addressObj = new JsonObject();
            addressObj.addProperty("addressLine1", addressLine1);
            addressObj.addProperty("addressLine2", addressLine2);
            addressObj.addProperty("mobile", mobile);
            addressObj.addProperty("postalCode", postalCode);
            addressObj.addProperty("email", email);
            addressObj.addProperty("city", city);

            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/add-address", gson.toJson(addressObj));
                if (jsonObject.get("message").getAsString().equals("success")) {
                    runOnUiThread(this::loadAddressData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadAddressData() {
        String email = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE).getString("email", "");
        new Thread(() -> {
            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-address", "{email:\"" + email + "\"}");
                if (jsonObject.get("message").getAsString().equals("success")) {
                    runOnUiThread(() -> {
                        RecyclerView addressRecyclerView = findViewById(R.id.ad_recyclerView);
                        ArrayList<AddressItem> addressItemsList = new ArrayList<>();
                        jsonObject.get("addresses").getAsJsonArray().forEach(address -> {
                            addressItemsList.add(new AddressItem(
                                    address.getAsJsonObject().get("addressId").getAsInt(),
                                    address.getAsJsonObject().get("address").getAsString(),
                                    address.getAsJsonObject().get("line1").getAsString(),
                                    address.getAsJsonObject().has("line2") ? address.getAsJsonObject().get("line2").getAsString() : "",
                                    address.getAsJsonObject().get("postalCode").getAsString(),
                                    address.getAsJsonObject().get("mobile").getAsString(),
                                    address.getAsJsonObject().get("city").getAsString()
                            ));
                        });

                        addressRecyclerView.setLayoutManager(new LinearLayoutManager(AddressActivity.this, RecyclerView.VERTICAL, false));
                        addressRecyclerView.setAdapter(new AddressItemAdapter(addressItemsList, addressItem -> {
                            SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("addressId", addressItem.getId());
                            editor.putString("address", addressItem.getAddress());
                            editor.putString("line1", addressItem.getLine1());
                            editor.putString("line2", addressItem.getLine2());
                            editor.putString("postalCode", addressItem.getPostalCode());
                            editor.putString("mobile", addressItem.getMobile());
                            editor.putString("city", addressItem.getCity());
                            editor.apply();
                            finish();
                        }));
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}