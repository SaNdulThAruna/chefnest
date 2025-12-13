package com.sandul.chefnest.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String API_URL = "https://sandul.tail45ad39.ts.net/ChefNest";

    private static final int TIMEOUT = 30;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build();

    public static JsonObject makeGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            if (response.isSuccessful()) {
                return gson.fromJson(response.body().string(), JsonObject.class);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static JsonObject makePostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_URL + url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            if (response.isSuccessful()) {
                return gson.fromJson(response.body().string(), JsonObject.class);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static JsonObject sendFormData(String url, MultipartBody formBody) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + url)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            if (response.isSuccessful()) {
                return gson.fromJson(response.body().string(), JsonObject.class);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}