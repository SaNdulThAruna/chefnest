package com.sandul.chefnest.model;

public class DietaryItem {

    String name;
    int resourceId;

    public DietaryItem() {
    }

    public DietaryItem(String name, int resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
