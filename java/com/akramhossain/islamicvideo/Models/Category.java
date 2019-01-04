package com.akramhossain.islamicvideo.Models;

/**
 * Created by Lenovo on 8/15/2018.
 */

public class Category {

    private String category_id;
    private String name;
    private String imageUrl;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
