package com.bhushan.vendor.model;

public class OrderListModel {

    private String orderId;
    private String ProductName;
    private String productImage;
    private String Price;
    private String date;
    private String status;


    public OrderListModel(String orderId, String productName, String productImage, String price, String date, String status) {
        this.orderId = orderId;
        ProductName = productName;
        this.productImage = productImage;
        Price = price;
        this.date = date;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
