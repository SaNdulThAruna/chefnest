package com.sandul.chefnest.ui.seller.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.adapter.SpinnerItemAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddProductActivity extends AppCompatActivity {

    private int dietaryId = 0;
    private int portionId = 0;
    private int cuisineId = 0;

    private Uri selectedImageUri1;
    private Uri selectedImageUri2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_product_constraintLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadDietarySpinners();
        loadCuisineSpinners();
        loadPortionSpinners();

        ImageView back = findViewById(R.id.dish_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ImageView dish1 = findViewById(R.id.dish_1);
        ImageView dish2 = findViewById(R.id.dish_2);

        ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(2), uris -> {
                    // Callback is invoked after the user selects media items or closes the
                    // photo picker.
                    if (!uris.isEmpty()) {

                        selectedImageUri1 = uris.get(0);
                        selectedImageUri2 = uris.get(1);
                        Glide.with(this).load(uris.get(0)).into(dish1);
                        Glide.with(this).load(uris.get(1)).into(dish2);
                        Log.d("PhotoPicker", "Selected URIs: " + uris);

                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        Button addDishImg = findViewById(R.id.add_img_btn);
        addDishImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());

            }
        });

        Button addDish = findViewById(R.id.button5);

        int productId = getIntent().getIntExtra("productId",0);
        if (productId != 0) {

            loadDish(String.valueOf(productId));

            // Edit product
            addDish.setText("Update Dish");
            addDish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateDish(String.valueOf(productId));
                }
            });

        } else {
            // Add product

            addDish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDish();
                }
            });
        }

    }

    private void loadDietarySpinners() {
        // Load spinners
        new Thread(() -> {
            try {

                JsonObject jsonObject = NetworkUtils.makeGetRequest("/load-dietary");

                if (jsonObject.get("message").getAsString().equals("success")) {


                    runOnUiThread(() -> {
                        // Load dietary spinners

                        ArrayList<SpinnerItem> dietaries = new ArrayList<>();
                        dietaries.add(new SpinnerItem(0, "Select Dietary"));
                        jsonObject.get("dietaries").getAsJsonArray().forEach(dietary -> {
                            // Load dietary spinners
                            JsonObject dietaryObject = dietary.getAsJsonObject();
                            dietaries.add(new SpinnerItem(dietaryObject.get("id").getAsInt(), dietaryObject.get("name").getAsString()));
                        });

                        Spinner spinnerDietary = findViewById(R.id.spinner_dietery);
                        SpinnerItemAdapter dietaryAdapter = new SpinnerItemAdapter(this, R.layout.viewholder_spinner, dietaries);
                        spinnerDietary.setAdapter(dietaryAdapter);
                        spinnerDietary.setSelection(0);
                        spinnerDietary.setDropDownVerticalOffset(100);

                        spinnerDietary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                dietaryId = parent.getSelectedItemPosition();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    });
                } else {
                    runOnUiThread(() -> {
                        Log.e("AddProductActivity", "loadDietarySpinners: " + jsonObject.get("message").getAsString());
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("AddProductActivity", "loadDietarySpinners: ", e);
                });
            }

        }).start();

    }

    private void loadCuisineSpinners() {
        // Load spinners
        new Thread(() -> {
            try {

                JsonObject jsonObject = NetworkUtils.makeGetRequest("/load-cuisine");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    runOnUiThread(() -> {
                        // Load cuisine spinners

                        ArrayList<SpinnerItem> cuisines = new ArrayList<>();
                        cuisines.add(new SpinnerItem(0, "Select Cuisine"));
                        jsonObject.get("cuisines").getAsJsonArray().forEach(cuisine -> {
                            // Load cuisine spinners
                            JsonObject cuisineObject = cuisine.getAsJsonObject();
                            cuisines.add(new SpinnerItem(cuisineObject.get("id").getAsInt(), cuisineObject.get("name").getAsString()));
                        });

                        Spinner spinnerDietary = findViewById(R.id.spinner_cuisine);
                        SpinnerItemAdapter cuisineAdapter = new SpinnerItemAdapter(this, R.layout.viewholder_spinner, cuisines);
                        spinnerDietary.setAdapter(cuisineAdapter);
                        spinnerDietary.setSelection(0);
                        spinnerDietary.setDropDownVerticalOffset(100);

                        spinnerDietary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                cuisineId = parent.getSelectedItemPosition();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    });
                } else {
                    runOnUiThread(() -> {
                        Log.e("AddProductActivity", "loadDietarySpinners: " + jsonObject.get("message").getAsString());
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("AddProductActivity", "loadDietarySpinners: ", e);
                });
            }

        }).start();

    }

    private void loadPortionSpinners() {
        // Load spinners
        new Thread(() -> {
            try {

                JsonObject jsonObject = NetworkUtils.makeGetRequest("/load-portion");

                if (jsonObject.get("message").getAsString().equals("success")) {

                    runOnUiThread(() -> {
                        // Load portion spinners

                        ArrayList<SpinnerItem> portions = new ArrayList<>();
                        portions.add(new SpinnerItem(0, "Select Portion"));
                        jsonObject.get("portions").getAsJsonArray().forEach(portion -> {
                            // Load portion spinners
                            JsonObject portionObject = portion.getAsJsonObject();
                            portions.add(new SpinnerItem(portionObject.get("id").getAsInt(), portionObject.get("name").getAsString()));
                        });

                        Spinner spinnerDietary = findViewById(R.id.spinner_portion);
                        SpinnerItemAdapter portionAdapter = new SpinnerItemAdapter(this, R.layout.viewholder_spinner, portions);
                        spinnerDietary.setAdapter(portionAdapter);
                        spinnerDietary.setSelection(0);
                        spinnerDietary.setDropDownVerticalOffset(100);

                        spinnerDietary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                portionId = parent.getSelectedItemPosition();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    });
                } else {
                    runOnUiThread(() -> {
                        Log.e("AddProductActivity", "loadDietarySpinners: " + jsonObject.get("message").getAsString());
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("AddProductActivity", "loadDietarySpinners: ", e);
                });
            }

        }).start();

    }

    private void loadDish(String productId){

        SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        new Thread(() -> {
            try {
                JsonObject jsonObject = NetworkUtils.makePostRequest("/load-chef-dish" , "{email:"+email+",dishId:"+productId+"}");

                if (jsonObject.get("message").getAsString().equals("success")) {
                    JsonObject dish = jsonObject.get("dish").getAsJsonObject();
                    runOnUiThread(() -> {
                        EditText title = findViewById(R.id.dish_title);
                        EditText description = findViewById(R.id.product_description);
                        EditText price = findViewById(R.id.dish_price);
                        EditText quantity = findViewById(R.id.dish_qty);

                        title.setText(dish.get("title").getAsString());
                        description.setText(dish.get("description").getAsString());
                        price.setText(dish.get("price").getAsString());
                        quantity.setText(dish.get("qty").getAsString());

                        Spinner spinnerDietary = findViewById(R.id.spinner_dietery);
                        spinnerDietary.setSelection(dish.get("dietary").getAsInt());

                        Spinner spinnerCuisine = findViewById(R.id.spinner_cuisine);
                        spinnerCuisine.setSelection(dish.get("cuisine").getAsInt());

                        Spinner spinnerPortion = findViewById(R.id.spinner_portion);
                        spinnerPortion.setSelection(dish.get("portion").getAsInt());

                        ImageView dish1 = findViewById(R.id.dish_1);
                        ImageView dish2 = findViewById(R.id.dish_2);

                        Glide.with(this).load(dish.get("image1Url").getAsString()).into(dish1);
                        Glide.with(this).load(dish.get("image2Url").getAsString()).into(dish2);

                        selectedImageUri1 = Uri.parse(dish.get("image1Url").getAsString());
                        selectedImageUri2 = Uri.parse(dish.get("image2Url").getAsString());

                    });
                } else {
                    runOnUiThread(() -> {
                        Log.e("AddProductActivity", "loadDish: " + jsonObject.get("message").getAsString());
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("AddProductActivity", "loadDish: ", e);
                });
            }

        }).start();

    }
    private void updateDish(String productId) {
        EditText title = findViewById(R.id.dish_title);
        EditText description = findViewById(R.id.product_description);
        EditText price = findViewById(R.id.dish_price);
        EditText quantity = findViewById(R.id.dish_qty);

        String titleStr = title.getText().toString();
        String descriptionStr = description.getText().toString();
        String priceStr = price.getText().toString();
        String quantityStr = quantity.getText().toString();
        String dietaryStr = String.valueOf(dietaryId);
        String cuisineStr = String.valueOf(cuisineId);
        String portionStr = String.valueOf(portionId);

        new Thread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try {
                MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id", productId)
                        .addFormDataPart("title", titleStr)
                        .addFormDataPart("description", descriptionStr)
                        .addFormDataPart("price", priceStr)
                        .addFormDataPart("qty", quantityStr)
                        .addFormDataPart("dietaryId", dietaryStr)
                        .addFormDataPart("cuisineId", cuisineStr)
                        .addFormDataPart("portionId", portionStr)
                        .addFormDataPart("email", email);

                if (selectedImageUri1 == null && selectedImageUri2 == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Please select images", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    if (selectedImageUri1 != null) {
                        Uri imageUri1 = handleImageUri(selectedImageUri1);
                        InputStream inputStream = getContentResolver().openInputStream(imageUri1);
                        byte[] imageBytes = new byte[inputStream.available()];
                        inputStream.read(imageBytes);
                        inputStream.close();
                        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                        formBodyBuilder.addFormDataPart("img1", imageUri1.getLastPathSegment(), imageBody);
                    }

                    if (selectedImageUri2 != null) {
                        Uri imageUri2 = handleImageUri(selectedImageUri2);
                        InputStream inputStream2 = getContentResolver().openInputStream(imageUri2);
                        byte[] imageBytes2 = new byte[inputStream2.available()];
                        inputStream2.read(imageBytes2);
                        inputStream2.close();
                        RequestBody imageBody2 = RequestBody.create(MediaType.parse("image/*"), imageBytes2);
                        formBodyBuilder.addFormDataPart("img2", imageUri2.getLastPathSegment(), imageBody2);
                    }

                    MultipartBody formBody = formBodyBuilder.build();
                    JsonObject jsonObject = NetworkUtils.sendFormData("/update-dish", formBody);

                    if (jsonObject.get("message").getAsString().equals("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Dish updated successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("AddProductActivity", "updateDish: ", e);
                });
            }
        }).start();
    }

    private Uri handleImageUri(Uri uri) throws IOException {
        if (uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
            return downloadImage(uri.toString());
        } else {
            return uri;
        }
    }

    private Uri downloadImage(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        File file = new File(getCacheDir(), "temp_image.jpg");
        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        }

        return Uri.fromFile(file);
    }

    private void addDish() {

        EditText title = findViewById(R.id.dish_title);
        EditText description = findViewById(R.id.product_description);
        EditText price = findViewById(R.id.dish_price);
        EditText quantity = findViewById(R.id.dish_qty);

        String titleStr = title.getText().toString();
        String descriptionStr = description.getText().toString();
        String priceStr = price.getText().toString();
        String quantityStr = quantity.getText().toString();
        String dietaryStr = String.valueOf(dietaryId);
        String cuisineStr = String.valueOf(cuisineId);
        String portionStr = String.valueOf(portionId);


        new Thread(() -> {

            SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            try {

                MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", titleStr)
                        .addFormDataPart("description", descriptionStr)
                        .addFormDataPart("price", priceStr)
                        .addFormDataPart("qty", quantityStr)
                        .addFormDataPart("dietaryId", dietaryStr)
                        .addFormDataPart("cuisineId", cuisineStr)
                        .addFormDataPart("portionId", portionStr)
                        .addFormDataPart("email", email);

                if (selectedImageUri1 == null && selectedImageUri2 == null) {

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Please select images", Toast.LENGTH_SHORT).show();
                    });

                    return;

                } else {

                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri1);
                    byte[] imageBytes = new byte[inputStream.available()];
                    inputStream.read(imageBytes);
                    inputStream.close();
                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                    formBodyBuilder.addFormDataPart("img1", selectedImageUri1.getLastPathSegment(), imageBody);

                    InputStream inputStream2 = getContentResolver().openInputStream(selectedImageUri2);
                    byte[] imageBytes2 = new byte[inputStream2.available()];
                    inputStream2.read(imageBytes2);
                    inputStream2.close();
                    RequestBody imageBody2 = RequestBody.create(MediaType.parse("image/*"), imageBytes2);
                    formBodyBuilder.addFormDataPart("img2", selectedImageUri2.getLastPathSegment(), imageBody2);

                    MultipartBody formBody = formBodyBuilder.build();
                    JsonObject jsonObject = NetworkUtils.sendFormData("/add-dish", formBody);

                    if (jsonObject.has("message") && jsonObject.get("message").getAsString().equals("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Dish added successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        });
                    } else if (jsonObject.has("address") && jsonObject.get("address").getAsString().equals("empty")) {

                        runOnUiThread(() -> {
                            new AlertDialog.Builder(AddProductActivity.this)
                                    .setTitle("Profile Incomplete")
                                    .setMessage("Before adding a dish, please complete your profile.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        // Navigate to edit profile
                                        Intent intent = new Intent(AddProductActivity.this, SellerEditProfileActivity.class);
                                        startActivity(intent);
                                    })
                                    .setCancelable(false)
                                    .show();
                        });

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e("AddProductActivity", "addDish: ", e);
                });
            }


        }).start();

    }

}