package com.sandul.chefnest.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpSingleton {
    private static OkHttpClient instance;

    private OkHttpSingleton() {}

    public static OkHttpClient getInstance() {
        if (instance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            instance = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
        }
        return instance;
    }
}
