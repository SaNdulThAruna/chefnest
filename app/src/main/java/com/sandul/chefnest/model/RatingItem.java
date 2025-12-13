package com.sandul.chefnest.model;

public class RatingItem {

    String customerName;
    String content;
    float rating;

    public RatingItem(String customerName, String content, float rating) {
        this.customerName = customerName;
        this.content = content;
        this.rating = rating;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
