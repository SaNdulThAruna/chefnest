package com.sandul.chefnest.ui.activity;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.model.User;
import com.sandul.chefnest.network.NetworkUtils;
import com.sandul.chefnest.ui.admin.activity.AdminActivity;
import com.sandul.chefnest.ui.customer.activity.HomeActivity;
import com.sandul.chefnest.ui.seller.activity.SellerActivity;
import com.sandul.chefnest.util.InternetBroadCastReceiver;
import com.sandul.chefnest.util.SQLiteHelper;

import java.io.IOException;

public class SignInActivity extends AppCompatActivity implements SensorEventListener {

    private boolean isPasswordVisible = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long mShakeTime = 0;
    private static final float SHAKE_THRESHOLD = 12.0f;
    private static final int SHAKE_WAIT_TIME_MS = 250;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - mShakeTime) > SHAKE_WAIT_TIME_MS) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

                if (acceleration > SHAKE_THRESHOLD) {
                    mShakeTime = curTime;
                    clearInputFields();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private InternetBroadCastReceiver internetBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            v.setPadding(0, 0, 0, 0);
            return insets;
        });

        internetBroadCastReceiver = new InternetBroadCastReceiver();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            Log.e("Sensor", "Sensor not found");
        }


        SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email != null) {
            String userType = sharedPreferences.getString("userType", null);
            if (userType.equals("Admin")) {
                startActivity(new Intent(SignInActivity.this, AdminActivity.class));
                finish();
            } else if (userType.equals("Chef")) {
                startActivity(new Intent(SignInActivity.this, SellerActivity.class));
                finish();
            } else if (userType.equals("Customer")) {
                startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                finish();
            }
        }

        ImageView imageView = findViewById(R.id.imageView1);
        ObjectAnimator shakeAnimator = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f);
        shakeAnimator.setDuration(3000);
        shakeAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        shakeAnimator.setRepeatMode(ObjectAnimator.RESTART);
        shakeAnimator.start();


        TextView signup = findViewById(R.id.signupTxt);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignInActivity.this, SignupActivity.class));

            }
        });

        Button loginButton = findViewById(R.id.loginBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (!isNetworkAvailable()) {
                    Toast.makeText(SignInActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                TextInputEditText email = findViewById(R.id.emailText);
                TextInputEditText password = findViewById(R.id.passwordText);


                Gson gson = new Gson();

                String emailString = email.getText().toString().toLowerCase().trim();
                String passwordString = password.getText().toString();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("email", emailString);
                jsonObject.addProperty("password", passwordString);


                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JsonObject responseJson = NetworkUtils.makePostRequest("/signin", gson.toJson(jsonObject));

                            if (responseJson.get("message").getAsString().equals("success")) {

                                User user = new User(
                                        emailString.toLowerCase().trim(),
                                        responseJson.get("firstName").getAsString(),
                                        responseJson.get("lastName").getAsString(),
                                        responseJson.get("mobile").getAsString(),
                                        responseJson.get("addressLine1").getAsString(),
                                        responseJson.get("addressLine2").getAsString(),
                                        responseJson.get("city").getAsInt(),
                                        responseJson.get("postalCode").getAsInt()
                                );

                                SQLiteHelper db = new SQLiteHelper(SignInActivity.this, "chefnest.db", null, 1);
                                SQLiteDatabase writableDatabase = db.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("email", user.getEmail());
                                values.put("first_name", user.getFirstName());
                                values.put("last_name", user.getLastName());
                                values.put("mobile", user.getMobile());
                                values.put("line1", user.getAddressLine1());
                                values.put("line2", user.getAddressLine2());
                                values.put("city", user.getCity());
                                values.put("postalcode", user.getPostalCode());
                                writableDatabase.insert("user", null, values);
                                writableDatabase.close();

                                SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email",emailString.toLowerCase().trim());
                                editor.putString("username",responseJson.get("username").getAsString());

                                String userType = responseJson.get("userType").getAsString();

                                if (userType.equals("Admin")) {
                                    editor.putString("userType", "Admin");
                                    editor.putInt("account_status", responseJson.get("account_status").getAsInt());
                                    editor.apply();
                                    startActivity(new Intent(SignInActivity.this, AdminActivity.class));
                                    finish();
                                } else if (userType.equals("Chef")) {
                                    editor.putString("userType", "Chef");
                                    editor.putInt("account_status", responseJson.get("account_status").getAsInt());
                                    editor.apply();
                                    startActivity(new Intent(SignInActivity.this, SellerActivity.class));
                                    finish();
                                } else if (userType.equals("Customer")) {
                                    editor.putString("userType", "Customer");
                                    editor.putInt("account_status", responseJson.get("account_status").getAsInt());
                                    editor.apply();
                                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                                    finish();
                                }

                            } else if (responseJson.get("message").getAsString().equals("unverified")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignInActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignInActivity.this, VerificationActivity.class);
                                        intent.putExtra("email", emailString);
                                        intent.putExtra("activity", "signin");
                                        startActivity(intent);
                                    }
                                });

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(SignInActivity.this, responseJson.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                        } catch (IOException e) {
                            runOnUiThread(()->{
                                Log.e("SignInActivity", "Error in making request", e);
                            });
                        }
                    }
                }).start();*/

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JsonObject responseJson = NetworkUtils.makePostRequest("/signin", gson.toJson(jsonObject));

                            if (responseJson.get("message").getAsString().equals("success")) {

                                String firstName = responseJson.get("firstName").getAsString();
                                String lastName = responseJson.get("lastName").getAsString();
                                String mobile = responseJson.has("mobile") ? responseJson.get("mobile").getAsString() : "";
                                String addressLine1 = responseJson.has("addressLine1") ? responseJson.get("addressLine1").getAsString() : "";
                                String addressLine2 = responseJson.has("addressLine2") ? responseJson.get("addressLine2").getAsString() : "";
                                int city = responseJson.has("city") ? responseJson.get("city").getAsInt() : 0;
                                int postalCode = responseJson.has("postalCode") ? responseJson.get("postalCode").getAsInt() : 0;


                                User user = new User(
                                        emailString.toLowerCase().trim(),
                                        responseJson.get("firstName").getAsString(),
                                        responseJson.get("lastName").getAsString(),
                                        mobile,
                                        addressLine1,
                                        addressLine2,
                                        city,
                                        postalCode
                                );

                                SQLiteHelper db = new SQLiteHelper(SignInActivity.this, "chefnest.db", null, 1);
                                SQLiteDatabase writableDatabase = db.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("email", user.getEmail());
                                values.put("first_name", user.getFirstName());
                                values.put("last_name", user.getLastName());
                                values.put("mobile", user.getMobile());
                                values.put("line1", user.getAddressLine1());
                                values.put("line2", user.getAddressLine2());
                                values.put("city", user.getCity());
                                values.put("postalcode", user.getPostalCode());

                                long result = writableDatabase.insert("user", null, values);
                                writableDatabase.close();

                                if (result == -1) {
                                    Log.e("SignInActivity", "Failed to insert user into database");
                                } else {
                                    Log.i("SignInActivity", "User inserted into database successfully");
                                }

                                SharedPreferences sharedPreferences = getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", emailString.toLowerCase().trim());
                                editor.putString("username", responseJson.get("username").getAsString());

                                String userType = responseJson.get("userType").getAsString();

                                if (userType.equals("Admin")) {
                                    editor.putString("userType", "Admin");
                                    editor.putInt("account_status", responseJson.get("account_status").getAsInt());
                                    editor.apply();
                                    startActivity(new Intent(SignInActivity.this, AdminActivity.class));
                                    finish();
                                } else if (userType.equals("Chef")) {
                                    editor.putString("userType", "Chef");
                                    editor.putInt("account_status", responseJson.get("account_status").getAsInt());
                                    editor.apply();
                                    startActivity(new Intent(SignInActivity.this, SellerActivity.class));
                                    finish();
                                } else if (userType.equals("Customer")) {
                                    editor.putString("userType", "Customer");
                                    editor.putInt("account_status", responseJson.get("account_status").getAsInt());
                                    editor.apply();
                                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                                    finish();
                                }

                            } else if (responseJson.get("message").getAsString().equals("unverified")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignInActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignInActivity.this, VerificationActivity.class);
                                        intent.putExtra("email", emailString);
                                        intent.putExtra("activity", "signin");
                                        startActivity(intent);
                                    }
                                });

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignInActivity.this, responseJson.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } catch (IOException e) {
                            runOnUiThread(() -> {
                                Log.e("SignInActivity", "Error in making request", e);
                            });
                        }
                    }
                }).start();
            }
        });

    }

    private void clearInputFields() {
        runOnUiThread(() -> {
            EditText email = findViewById(R.id.emailText);
            EditText password = findViewById(R.id.passwordText);
            email.setText("");
            password.setText("");
            Toast.makeText(SignInActivity.this, "Fields cleared!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(internetBroadCastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internetBroadCastReceiver);
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    /*private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/


}