package com.sandul.chefnest.model;

public class CartItem {

    private int id;
    private String cartItemTitle;
    private String cartItemPrice;
    private String cartItemQty;
    private String cartItemImage;


    public CartItem(int id, String cartItemTitle, String cartItemPrice, String cartItemQty, String cartItemImage) {
        this.id = id;
        this.cartItemTitle = cartItemTitle;
        this.cartItemPrice = cartItemPrice;
        this.cartItemQty = cartItemQty;
        this.cartItemImage = cartItemImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCartItemTitle() {
        return cartItemTitle;
    }

    public void setCartItemTitle(String cartItemTitle) {
        this.cartItemTitle = cartItemTitle;
    }

    public String getCartItemPrice() {
        return cartItemPrice;
    }

    public void setCartItemPrice(String cartItemPrice) {
        this.cartItemPrice = cartItemPrice;
    }

    public String getCartItemQty() {
        return cartItemQty;
    }

    public void setCartItemQty(String cartItemQty) {
        this.cartItemQty = cartItemQty;
    }

    public String getCartItemImage() {
        return cartItemImage;
    }

    public void setCartItemImage(String cartItemImage) {
        this.cartItemImage = cartItemImage;
    }
}
