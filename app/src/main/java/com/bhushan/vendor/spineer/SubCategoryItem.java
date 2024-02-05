package com.bhushan.vendor.spineer;

public class SubCategoryItem {
    private int subCategoryId;
    private String cat_id;
    private String subCategoryName;
    private String img;

    public SubCategoryItem(int subCategoryId, String cat_id, String subCategoryName, String img) {
        this.subCategoryId = subCategoryId;
        this.cat_id = cat_id;
        this.subCategoryName = subCategoryName;
        this.img = img;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
