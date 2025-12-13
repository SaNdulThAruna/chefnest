package com.sandul.chefnest.ui.seller.fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sandul.chefnest.R;
import com.sandul.chefnest.network.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SellerVerificationFragment extends Fragment {

    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.verification_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                result -> {
                    if (result != null) {
                        selectedImageUri = result;
                        ImageView imageView = getView().findViewById(R.id.imageView15);
//                        Glide.with(this).load(result).into(imageView);
                        Picasso.get().load(result).into(imageView);
                        imageView.setImageURI(result);
                        Log.d("PhotoPicker", "Selected URI: " + result);
                    } else {
                        Log.d("PhotoPicker", "No image selected");
                    }
                }
        );

        ImageView proof = view.findViewById(R.id.imageView15);

        proof.setOnClickListener(V -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });


        Button verify = getView().findViewById(R.id.button4);
        verify.setOnClickListener(V -> verifySeller());

    }

    private void verifySeller() {

        new Thread(()->{

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.sandul.chefnest.data", 0);
            String email = sharedPreferences.getString("email", "");

            MultipartBody.Builder formBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", email);

            if (selectedImageUri != null) {
                try (InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedImageUri)) {
                    if (inputStream != null) {
                        byte[] imageBytes = new byte[inputStream.available()];
                        inputStream.read(imageBytes);
                        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                        formBodyBuilder.addFormDataPart("proof", selectedImageUri.getLastPathSegment(), imageBody);
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Failed to open image", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (IOException e) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error reading image", Toast.LENGTH_SHORT).show();
                    });
                    Log.e("SellerVerificationFragment", "Error reading image", e);
                }
            }else {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            MultipartBody formBody = formBodyBuilder.build();
            try {

                JsonObject jsonObject = NetworkUtils.sendFormData("/chef-verification", formBody);

                if (jsonObject.get("message").getAsString().equals("success")) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Verification request sent", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Log.e("SellerVerificationFragment", "Error verifying seller: " + jsonObject.get("message").getAsString());
                    });
                }

            } catch (IOException e) {
                getActivity().runOnUiThread(() -> {
                    Log.e("SellerVerificationFragment", "Error verifying seller", e);
                });
            }

        }).start();

    }
}