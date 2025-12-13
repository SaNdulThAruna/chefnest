package com.sandul.chefnest.model;

public class SellerNewOrder {

    private String Id;
    private String title;
    private String qty;
    private String img;

    public SellerNewOrder(String id, String title, String qty, String img) {
        Id = id;
        this.title = title;
        this.qty = qty;
        this.img = img;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
