package com.sandul.chefnest.model;

public class SellerOrderItem {

    private String orderTitle;
    private String orderId;
    private String orderQty;
    private String orderPrice;
    private String orderStatusId;
    private String orderStatus;
    private String customerName;
    private String customerPostalCodeCityName;
    private String orderImg;

    public SellerOrderItem(String orderTitle, String orderId, String orderQty, String orderPrice, String orderStatusId, String orderStatus, String customerName, String customerPostalCodeCityName, String orderImg) {
        this.orderTitle = orderTitle;
        this.orderId = orderId;
        this.orderQty = orderQty;
        this.orderPrice = orderPrice;
        this.orderStatusId = orderStatusId;
        this.orderStatus = orderStatus;
        this.customerName = customerName;
        this.customerPostalCodeCityName = customerPostalCodeCityName;
        this.orderImg = orderImg;
    }

    public String getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(String orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(String orderQty) {
        this.orderQty = orderQty;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPostalCodeCityName() {
        return customerPostalCodeCityName;
    }

    public void setCustomerPostalCodeCityName(String customerPostalCodeCityName) {
        this.customerPostalCodeCityName = customerPostalCodeCityName;
    }

    public String getOrderImg() {
        return orderImg;
    }

    public void setOrderImg(String orderImg) {
        this.orderImg = orderImg;
    }
}
