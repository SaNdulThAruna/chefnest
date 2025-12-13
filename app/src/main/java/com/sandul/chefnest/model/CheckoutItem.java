package com.sandul.chefnest.model;

public class CheckoutItem {

    private int id;
    private String title;
    private int qty;
    private double price;
    private String img;

    public CheckoutItem(int id, String title, int qty, double price, String img) {
        this.id = id;
        this.title = title;
        this.qty = qty;
        this.price = price;
        this.img = img;
    }

    public CheckoutItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
