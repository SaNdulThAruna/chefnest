package com.sandul.chefnest.model;

public class FoodItem {

    private int id;
    private String foodItemTitle;
    private String foodItemPrice;
    private String foodItemLocation;
    private String foodItemImage;

    public FoodItem(int id, String foodItemTitle, String foodItemPrice, String foodItemLocation, String foodItemImage) {
        this.id = id;
        this.foodItemTitle = foodItemTitle;
        this.foodItemPrice = foodItemPrice;
        this.foodItemLocation = foodItemLocation;
        this.foodItemImage = foodItemImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoodItemTitle() {
        return foodItemTitle;
    }

    public void setFoodItemTitle(String foodItemTitle) {
        this.foodItemTitle = foodItemTitle;
    }


    public String getFoodItemPrice() {
        return foodItemPrice;
    }

    public void setFoodItemPrice(String foodItemPrice) {
        this.foodItemPrice = foodItemPrice;
    }

    public String getFoodItemLocation() {
        return foodItemLocation;
    }

    public void setFoodItemLocation(String foodItemLocation) {
        this.foodItemLocation = foodItemLocation;
    }

    public String getFoodItemImage() {
        return foodItemImage;
    }

    public void setFoodItemImage(String foodItemImage) {
        this.foodItemImage = foodItemImage;
    }
}
