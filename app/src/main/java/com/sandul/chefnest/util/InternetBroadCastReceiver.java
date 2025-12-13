package com.sandul.chefnest.util;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.sandul.chefnest.R;

public class InternetBroadCastReceiver extends BroadcastReceiver {
    private AlertDialog dialog;

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isNetworkAvailable(context)) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } else {
            showNoInternetDialog(context);
        }
    }

    private void showNoInternetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.no_internet_dialog, null);

        Button btnRetry = view.findViewById(R.id.btnRetry);

        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnRetry.setOnClickListener(v -> {
            if (isNetworkAvailable(context)) {
                dialog.dismiss();
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
}