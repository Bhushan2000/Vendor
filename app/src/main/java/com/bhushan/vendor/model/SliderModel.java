package com.bhushan.vendor.model;

public class SliderModel {

        private String productID;
        private String productImage;
        private String productName;


    public SliderModel(String productID, String productImage, String productName) {
        this.productID = productID;
        this.productImage = productImage;
        this.productName = productName;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
