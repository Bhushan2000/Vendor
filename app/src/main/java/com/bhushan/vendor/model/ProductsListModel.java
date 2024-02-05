package com.bhushan.vendor.model;

import java.util.Collections;
import java.util.Comparator;

public class ProductsListModel {
    private String productID;
    private String productImage;
    private String productName;
    private int productPriceOriginal;
    private double discountPercentage;


    public ProductsListModel(String productID, String productImage, String productName, int productPriceOriginal, double discountPercentage) {
        this.productID = productID;
        this.productImage = productImage;
        this.productName = productName;
        this.productPriceOriginal = productPriceOriginal;
        this.discountPercentage = discountPercentage;
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

    public int getProductPriceOriginal() {
        return productPriceOriginal;
    }

    public void setProductPriceOriginal(int productPriceOriginal) {
        this.productPriceOriginal = productPriceOriginal;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}
