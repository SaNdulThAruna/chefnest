package com.sandul.chefnest.model;

public class AddressItem {

    private int id;
    private String address;
    private String line1;
    private String line2;
    private String postalCode;
    private String mobile;
    private String city;

    public AddressItem(int id, String address, String line1, String line2, String postalCode, String mobile, String city) {
        this.id = id;
        this.address = address;
        this.line1 = line1;
        this.line2 = line2;
        this.postalCode = postalCode;
        this.mobile = mobile;
        this.city = city;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
