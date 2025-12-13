package com.sandul.chefnest.ui.customer.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sandul.chefnest.R;
import com.sandul.chefnest.ui.adapter.NotificationAdapter;
import com.sandul.chefnest.model.NotificationItem;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView notificaRecyclerView = getView().findViewById(R.id.notification_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notificaRecyclerView.setLayoutManager(layoutManager);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<NotificationItem> notificationItems = new ArrayList<>();

        db.collection("notification")
                .whereEqualTo("email",email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            for (int i = 0; i < task.getResult().size(); i++) {
                                notificationItems.add(new NotificationItem(
                                        task.getResult().getDocuments().get(i).getString("title"),
                                        task.getResult().getDocuments().get(i).getString("msg"),
                                        Objects.requireNonNull(task.getResult().getDocuments().get(i).getDate("date")).toString().split(" ")[0],
                                        task.getResult().getDocuments().get(i).getString("imgUrl")
                                ));
                            }
                            notificaRecyclerView.setAdapter(new NotificationAdapter(notificationItems));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseFirestore", "Error getting documents.", e);
                });


        notificaRecyclerView.setAdapter(new NotificationAdapter(notificationItems));
    }

}