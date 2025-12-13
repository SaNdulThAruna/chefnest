package com.sandul.chefnest.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sandul.chefnest.databinding.ViewholderCartItemBinding;
import com.sandul.chefnest.model.CartItem;
import com.sandul.chefnest.network.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.Viewholder> {

    private ArrayList<CartItem> cartItems;
    private Activity activity;
    private TextView subTotalView;
    private double subTotal;

    public CartItemAdapter(Activity activity, ArrayList<CartItem> cartItems, TextView subTotalView, double subtotal) {
        this.cartItems = cartItems;
        this.activity = activity;
        this.subTotalView = subTotalView;
        this.subTotal = subtotal;
    }

    @NonNull
    @Override
    public CartItemAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewholderCartItemBinding binding = ViewholderCartItemBinding.inflate(layoutInflater, parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.Viewholder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void removeItem(int position) {
        cartItems.remove(position);
        notifyItemRemoved(position);
    }

    public void updatePrice(double price) {

        subTotalView.setText(String.format("Rs. %s", String.valueOf(price)));

    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private final ViewholderCartItemBinding binding;

        public Viewholder(@NonNull ViewholderCartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CartItem cartItem) {
            binding.cartItemTitle.setText(cartItem.getCartItemTitle());
            binding.cartItemPrice.setText(cartItem.getCartItemPrice());
            binding.cartItemQty.setText(cartItem.getCartItemQty());

            Glide.with(binding.getRoot().getContext())
                    .load(cartItem.getCartItemImage())
                    .into(binding.cartItemImg);

            binding.cartItemRemove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    new Thread(() -> {
                        try {
                            SharedPreferences sharedPreferences = binding.getRoot().getContext().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                            String email = sharedPreferences.getString("email", "");

                            JsonObject jsonObject = NetworkUtils.makePostRequest("/delete-cart-item", "{email:\"" + email + "\", cartId:" + cartItem.getId() + "}");
                            if (jsonObject.get("message").getAsString().equals("success")) {
                                activity.runOnUiThread(() -> {

                                    removeItem(position);

                                    updatePrice(subTotal -= Double.parseDouble(cartItem.getCartItemPrice()) * Integer.parseInt(cartItem.getCartItemQty()));

                                    Toast.makeText(activity, "Item removed from cart", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                activity.runOnUiThread(() -> {
                                    Toast.makeText(activity, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (IOException e) {
                            Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
                        }
                    }).start();
                }
            });

            binding.cartItemPlus.setOnClickListener(v -> {

                int qty = Integer.parseInt(binding.cartItemQty.getText().toString());
                qty++;
                int finalQty = qty;
                new Thread(() -> {
                    try {
                        SharedPreferences sharedPreferences = binding.getRoot().getContext().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                        String email = sharedPreferences.getString("email", "");

                        JsonObject jsonObject = NetworkUtils.makePostRequest("/update-cart-item", "{email:\"" + email + "\", cartId:" + cartItem.getId() + ", qty:" + finalQty + "}");
                        if (jsonObject.get("message").getAsString().equals("success")) {
                            activity.runOnUiThread(() -> {
                                binding.cartItemQty.setText(String.valueOf(finalQty));
                                cartItem.setCartItemQty(String.valueOf(finalQty));
                                subTotal += Double.parseDouble(cartItem.getCartItemPrice());
                                updatePrice(subTotal);
                                Toast.makeText(activity, "Item quantity updated", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            activity.runOnUiThread(() -> {
                                Toast.makeText(activity, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (IOException e) {
                        Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
                    }
                }).start();
            });

            binding.cartItemMinus.setOnClickListener(v -> {
                int qty = Integer.parseInt(binding.cartItemQty.getText().toString());
                if (qty > 1) {
                    qty--;
                    int finalQty = qty;
                    new Thread(() -> {
                        try {
                            SharedPreferences sharedPreferences = binding.getRoot().getContext().getSharedPreferences("com.sandul.chefnest.data", Context.MODE_PRIVATE);
                            String email = sharedPreferences.getString("email", "");

                            JsonObject jsonObject = NetworkUtils.makePostRequest("/update-cart-item", "{email:\"" + email + "\", cartId:" + cartItem.getId() + ", qty:" + finalQty + "}");
                            if (jsonObject.get("message").getAsString().equals("success")) {
                                activity.runOnUiThread(() -> {

                                    cartItem.setCartItemQty(String.valueOf(finalQty));
                                    binding.cartItemQty.setText(String.valueOf(finalQty));
                                    subTotal -= Double.parseDouble(cartItem.getCartItemPrice());
                                    updatePrice(subTotal);
                                    Toast.makeText(activity, "Item quantity updated", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                activity.runOnUiThread(() -> {
                                    Toast.makeText(activity, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (IOException e) {
                            Log.e("ChefNestLog", Objects.requireNonNull(e.getMessage()));
                        }
                    }).start();
                } else {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "You can't order less than 1", Toast.LENGTH_SHORT).show();
                    });
                }

            });
        }
    }
}