package com.sandul.chefnest.model;

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String mobile;
    private String addressLine1;
    private String addressLine2;
    private int city;
    private int postalCode;

    // Constructor, getters, and setters
    public User(String email, String firstName, String lastName, String mobile, String addressLine1, String addressLine2, int city, int postalCode) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public int getCity() {
        return city;
    }

    public int getPostalCode() {
        return postalCode;
    }
}
