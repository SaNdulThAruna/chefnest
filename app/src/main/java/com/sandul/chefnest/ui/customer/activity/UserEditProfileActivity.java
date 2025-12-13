package com.sandul.chefnest.ui.customer.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.SpinnerItem;
import com.sandul.chefnest.model.User;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.SpinnerItemAdapter;
import com.sandul.chefnest.util.SQLiteHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserEditProfileActivity extends AppCompatActivity {

    private Uri selectedImageUri;
    private int cityId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_update_profile_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /*Load cities*/
        loadCities();

        Button updateImgButton = findViewById(R.id.update_img);

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                result -> {
                    if (result != null) {
                        selectedImageUri = result;
                        ImageView imageView = findViewById(R.id.imageView9);
                        Glide.with(this).load(result).into(imageView);
                        imageView.setImageURI(result);
                        Log.d("PhotoPicker", "Selected URI: " + result);
                    } else {
                        Log.d("PhotoPicker", "No image selected");
                    }
                }
        );

        updateImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        Button updateProfileButton = findViewById(R.id.profile_update);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        ImageView backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateProfile() {

        EditText firstNameEditText = findViewById(R.id.user_first_name);
        EditText lastNameEditText = findViewById(R.id.user_last_name);
        EditText userEmailEditText = findViewById(R.id.user_email);
        EditText addressLine1EditText = findViewById(R.id.user_address_line1);
        EditText addressLine2EditText = findViewById(R.id.user_address_line2);
        EditText postalCodeEditText = findViewById(R.id.user_postal_code);
        EditText mobileEditText = findViewById(R.id.user_mobile);

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
                    SQLiteDatabase db = new SQLiteHelper(UserEditProfileActivity.this, "chefnest.db", null, 1).getWritableDatabase();
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
                        Toast.makeText(com.sandul.chefnest.ui.customer.activity.UserEditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(com.sandul.chefnest.ui.customer.activity.UserEditProfileActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_LONG).show();
                    });
                }
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(com.sandul.chefnest.ui.customer.activity.UserEditProfileActivity.this, "An Error occurred", Toast.LENGTH_LONG).show();
                });
            }
        }).start();

    }

    private void loadCities() {
        Spinner citySpinner = findViewById(R.id.cityspinner);

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
                        SpinnerItemAdapter priceSpinnerAdapter = new SpinnerItemAdapter(this, R.layout.viewholder_spinner, cityList);
                        citySpinner.setAdapter(priceSpinnerAdapter);
                        citySpinner.setSelection(0);
                        citySpinner.setDropDownVerticalOffset(100);

                        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                cityId = parent.getSelectedItemPosition();
//                                Toast.makeText(UserEditProfileActivity.this, "Selected city: " + String.valueOf(cityId), Toast.LENGTH_SHORT).show();
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
                throw new RuntimeException(e);
            }

            SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", null);

            try {
                /*JsonObject jsonObject = NetworkUtils.makePostRequest("/load-user-profile", "{email:" + email + "}");

                if (jsonObject.get("message").getAsString().equals("success")) {
                    runOnUiThread(() -> {
                        EditText firstName = findViewById(R.id.user_first_name);
                        EditText lastName = findViewById(R.id.user_last_name);
                        EditText userEmail = findViewById(R.id.user_email);
                        EditText addressLine1 = findViewById(R.id.user_address_line1);
                        EditText addressLine2 = findViewById(R.id.user_address_line2);
                        EditText postalCode = findViewById(R.id.user_postal_code);
                        EditText mobile = findViewById(R.id.user_mobile);
                        ImageView dp = findViewById(R.id.imageView9);

                        firstName.setText(jsonObject.get("firstName").getAsString());
                        lastName.setText(jsonObject.get("lastName").getAsString());
                        userEmail.setText(jsonObject.get("email").getAsString());

                        if (jsonObject.has("address") && !jsonObject.get("address").isJsonNull()) {
                            JsonObject address = jsonObject.getAsJsonObject("address");
                            addressLine1.setText(address.get("line1").getAsString());
                            if (address.has("line2") && !address.get("line2").isJsonNull() && !address.get("line2").getAsString().isEmpty()) {
                                addressLine2.setText(address.get("line2").getAsString());
                            }
                            if (address.has("city") && !address.get("city").isJsonNull()) {
                                citySpinner.setSelection(address.getAsJsonObject("city").get("id").getAsInt());
                            }
                            postalCode.setText(address.get("postalCode").getAsString());
                            mobile.setText(address.get("mobile").getAsString());
                        }
                        if (jsonObject.has("profileImg")) {
                            Glide.with(this).load(jsonObject.get("profileImg").getAsString()).into(dp);
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "An Error occurred", Toast.LENGTH_SHORT).show();
                    });
                }*/

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
                                    ImageView dp = findViewById(R.id.imageView9);
                                    Glide.with(this).load(dpUrl).into(dp);
                                });
                            }
                        } catch (IOException e) {
                            Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
                        }

                        runOnUiThread(() -> {
                            EditText firstName = findViewById(R.id.user_first_name);
                            EditText lastName = findViewById(R.id.user_last_name);
                            EditText userEmail = findViewById(R.id.user_email);
                            EditText addressLine1 = findViewById(R.id.user_address_line1);
                            EditText addressLine2 = findViewById(R.id.user_address_line2);
                            EditText postalCode = findViewById(R.id.user_postal_code);
                            EditText mobile = findViewById(R.id.user_mobile);

                            firstName.setText(cursor.getString(firstNameIndex));
                            lastName.setText(cursor.getString(lastNameIndex));
                            userEmail.setText(cursor.getString(emailIndex));
                            addressLine1.setText(cursor.getString(line1Index));
                            addressLine2.setText(cursor.getString(line2Index));
                            citySpinner.setSelection(cursor.getInt(cityIndex));
                            postalCode.setText(cursor.getString(postalCodeIndex));
                            mobile.setText(cursor.getString(mobileIndex));
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