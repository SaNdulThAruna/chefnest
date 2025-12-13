package com.sandul.chefnest.ui.seller.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.SpinnerItem;
import com.sandul.chefnest.model.User;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.SpinnerItemAdapter;
import com.sandul.chefnest.ui.customer.activity.UserEditProfileActivity;
import com.sandul.chefnest.util.SQLiteHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SellerEditProfileActivity extends AppCompatActivity {

    private Uri selectedImageUri;
    private int cityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_drawer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView back = findViewById(R.id.seller_edit_back);
        back.setOnClickListener(V -> finish());

        /*load Cities*/
        loadCity();


        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                result -> {
                    if (result != null) {
                        selectedImageUri = result;
                        ImageView imageView = findViewById(R.id.seller_imageView9);
//                        Glide.with(this).load(result).into(imageView);
                        Picasso.get().load(result).into(imageView);
                        imageView.setImageURI(result);
                        Log.d("PhotoPicker", "Selected URI: " + result);
                    } else {
                        Log.d("PhotoPicker", "No image selected");
                    }
                }
        );

        Button button = findViewById(R.id.seller_update_img);
        button.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        Button seller_profile_update = findViewById(R.id.seller_profile_update);
        seller_profile_update.setOnClickListener(V -> updateProfile());

    }


    private void updateProfile() {

        EditText firstNameEditText = findViewById(R.id.seller_firstName);
        EditText lastNameEditText = findViewById(R.id.seller_lastName);
        EditText userEmailEditText = findViewById(R.id.seller_email);
        EditText mobileEditText = findViewById(R.id.seller_mobile);
        EditText addressLine1EditText = findViewById(R.id.seller_address_line1);
        EditText addressLine2EditText = findViewById(R.id.seller_address_line2);
        EditText postalCodeEditText = findViewById(R.id.seller_postal_code);

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String addressLine1 = addressLine1EditText.getText().toString();
        String addressLine2 = addressLine2EditText.getText().toString();
        String city = String.valueOf(cityId);
        String postalCode = postalCodeEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();

        new Thread(() -> {
            try {
                MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("firstName", firstName)
                        .addFormDataPart("lastName", lastName)
                        .addFormDataPart("email", email)
                        .addFormDataPart("addressLine1", addressLine1)
                        .addFormDataPart("addressLine2", addressLine2)
                        .addFormDataPart("city", city)
                        .addFormDataPart("postalCode", postalCode)
                        .addFormDataPart("mobile", mobile);

                if (selectedImageUri != null) {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    byte[] imageBytes = new byte[inputStream.available()];
                    inputStream.read(imageBytes);
                    inputStream.close();
                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                    formBodyBuilder.addFormDataPart("profileImage", selectedImageUri.getLastPathSegment(), imageBody);
                }

                MultipartBody formBody = formBodyBuilder.build();
                JsonObject jsonObject = NetworkUtils.sendFormData("/update-user-profile", formBody);

                if (jsonObject.get("message").getAsString().equals("success")) {
                    SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.putString("username", firstName + " " + lastName);
                    editor.apply();

                    User user = new User(email, firstName, lastName, mobile, addressLine1, addressLine2, cityId, Integer.parseInt(postalCode));
                    SQLiteDatabase db = new SQLiteHelper(SellerEditProfileActivity.this, "chefnest.db", null, 1).getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("email", user.getEmail());
                    values.put("first_name", user.getFirstName());
                    values.put("last_name", user.getLastName());
                    values.put("mobile", user.getMobile());
                    values.put("line1", user.getAddressLine1());
                    values.put("line2", user.getAddressLine2());
                    values.put("city", user.getCity());
                    values.put("postalcode", user.getPostalCode());
                    db.update("user", values, "email = ?", new String[]{user.getEmail()});

                    runOnUiThread(() -> {
                        Toast.makeText(SellerEditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SellerEditProfileActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_LONG).show();
                    });
                }
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Log.e("SellerEditProfile", e.getMessage());
                });
            }
        }).start();

    }

    private void loadCity() {
        new Thread(() -> {

            try {
                JsonObject jsonObject = NetworkUtils.makeGetRequest("/load-city");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    ArrayList<SpinnerItem> cityList = new ArrayList<>();
                    cityList.add(new SpinnerItem(0, "Select City"));
                    jsonObject.get("cities").getAsJsonArray().forEach(city -> {
                        cityList.add(new SpinnerItem(city.getAsJsonObject().get("id").getAsInt(), city.getAsJsonObject().get("name").getAsString()));
                    });

                    runOnUiThread(() -> {
                        Spinner citySpinner = findViewById(R.id.spinner);

                        SpinnerItemAdapter priceSpinnerAdapter = new SpinnerItemAdapter(this, R.layout.viewholder_spinner, cityList);
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

                } else {
                    runOnUiThread(() -> {
                        Log.e("ChefNestLog", jsonObject.get("message").getAsString());
                    });
                }

            } catch (IOException e) {
                runOnUiThread(() -> {
                    Log.e("SellerEditProfile", e.getMessage());
                });
            }

        }).start();

        SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        new Thread(() -> {

            /*try {

                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-user-profile", "{email:" + email + "}");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    runOnUiThread(() -> {
                        //load user data

                        EditText seller_firstName = findViewById(R.id.seller_firstName);
                        EditText seller_lastName = findViewById(R.id.seller_lastName);
                        EditText seller_email = findViewById(R.id.seller_email);
                        EditText seller_mobile = findViewById(R.id.seller_mobile);
                        EditText seller_address_line1 = findViewById(R.id.seller_address_line1);
                        EditText seller_address_line2 = findViewById(R.id.seller_address_line2);
                        Spinner citySpinner = findViewById(R.id.spinner);
                        EditText seller_postal_code = findViewById(R.id.seller_postal_code);
                        ImageView seller_dp = findViewById(R.id.seller_imageView9);

                        seller_email.setEnabled(false);

                        seller_firstName.setText(jsonObject.get("firstName").getAsString());
                        seller_lastName.setText(jsonObject.get("lastName").getAsString());
                        seller_email.setText(jsonObject.get("email").getAsString());


                        if (jsonObject.has("address")) {
                            JsonObject address = jsonObject.get("address").getAsJsonObject();
                            seller_address_line1.setText(address.get("line1").getAsString());
                            seller_postal_code.setText(address.get("postalCode").getAsString());
                            citySpinner.setSelection(address.get("city").getAsJsonObject().get("id").getAsInt());

                            if (address.has("line2") && !address.get("line2").isJsonNull() && !address.get("line2").getAsString().isEmpty()) {
                                seller_address_line2.setText(address.get("line2").getAsString());
                            }
                            seller_mobile.setText(address.get("mobile").getAsString());
                        }

                        if (jsonObject.has("profileImg")) {
//                            Glide.with(SellerEditProfileActivity.this).load(jsonObject.get("profileImg").getAsString()).into(seller_dp);
                            Picasso.get().load(jsonObject.get("profileImg").getAsString()).into(seller_dp);
                        }
                    });

                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(SellerEditProfileActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("SellerEditProfileActivity", e.getMessage());
                });
            }*/

            try {

                SQLiteHelper dbHelper = new SQLiteHelper(this, "chefnest.db", null, 1);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{email});
                if (cursor != null && cursor.moveToFirst()) {

                    int firstNameIndex = cursor.getColumnIndex("first_name");
                    int lastNameIndex = cursor.getColumnIndex("last_name");
                    int emailIndex = cursor.getColumnIndex("email");
                    int line1Index = cursor.getColumnIndex("line1");
                    int line2Index = cursor.getColumnIndex("line2");
                    int cityIndex = cursor.getColumnIndex("city");
                    int postalCodeIndex = cursor.getColumnIndex("postalcode");
                    int mobileIndex = cursor.getColumnIndex("mobile");

                    if (firstNameIndex != -1 && lastNameIndex != -1 && emailIndex != -1 &&
                            line1Index != -1 && line2Index != -1 && cityIndex != -1 &&
                            postalCodeIndex != -1 && mobileIndex != -1) {


                        try {
                            JsonObject jsonObject = NetworkUtils.makePostRequest("/load-dp", "{email:" + email + "}");
                            if (jsonObject.get("message").getAsString().equals("success")) {
                                String dpUrl = jsonObject.has("profileImg") ? jsonObject.get("profileImg").getAsString() : "";
                                runOnUiThread(() -> {
                                    ImageView dp = findViewById(R.id.seller_imageView9);
                                    Glide.with(this).load(dpUrl.isEmpty() ? R.drawable.user_img : dpUrl).into(dp);
                                });
                            }
                        } catch (IOException e) {
                            Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
                        }

                        runOnUiThread(() -> {

                            EditText seller_firstName = findViewById(R.id.seller_firstName);
                            EditText seller_lastName = findViewById(R.id.seller_lastName);
                            EditText seller_email = findViewById(R.id.seller_email);
                            EditText seller_mobile = findViewById(R.id.seller_mobile);
                            EditText seller_address_line1 = findViewById(R.id.seller_address_line1);
                            EditText seller_address_line2 = findViewById(R.id.seller_address_line2);
                            Spinner citySpinner = findViewById(R.id.spinner);
                            EditText seller_postal_code = findViewById(R.id.seller_postal_code);

                            seller_firstName.setText(cursor.getString(firstNameIndex));
                            seller_lastName.setText(cursor.getString(lastNameIndex));
                            seller_email.setText(cursor.getString(emailIndex));
                            seller_address_line1.setText(cursor.getString(line1Index));
                            seller_address_line2.setText(cursor.getString(line2Index));
                            citySpinner.setSelection(cursor.getInt(cityIndex));
                            seller_postal_code.setText(cursor.getString(postalCodeIndex));
                            seller_mobile.setText(cursor.getString(mobileIndex));
                        });
                    } else {
                        // Handle the case where one or more columns do not exist
                        runOnUiThread(() -> {
                            Toast.makeText(this, "One or more columns do not exist in the database", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

            } catch (Exception e) {
                Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
            }

        }).start();
    }
}