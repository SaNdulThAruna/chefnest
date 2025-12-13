package com.sandul.chefnest.model;

public class OrderItem {

    private int orderID;
    private String orderTitle;
    private String orderPrice;
    private String orderStatusId;
    private String orderStatus;
    private String orderImg;

    public OrderItem(int orderID, String orderTitle, String orderPrice, String orderStatusId, String orderStatus, String orderImg) {
        this.orderID = orderID;
        this.orderTitle = orderTitle;
        this.orderPrice = orderPrice;
        this.orderStatusId = orderStatusId;
        this.orderStatus = orderStatus;
        this.orderImg = orderImg;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(String orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getOrderImg() {
        return orderImg;
    }

    public void setOrderImg(String orderImg) {
        this.orderImg = orderImg;
    }
}
