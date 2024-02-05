package com.bhushan.vendor.spineer;

public class CategoryItem {
    private String categoryName;
    private String categoryImage;
    private int categoryId;


    public CategoryItem(String categoryName, String categoryImage, int categoryId) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
