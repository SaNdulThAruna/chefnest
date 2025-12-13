package com.sandul.chefnest.model;

public class Product {

    private int productId;
    private String productTitle;
    private String productPrice;
    private String productImg;

    public Product(int productId, String productTitle, String productPrice, String productImg) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productImg = productImg;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }
}
