package com.akramhossain.islamicvideo.Models;

/**
 * Created by Lenovo on 8/15/2018.
 */

public class RecentlyWatched {

    private String video_id;
    private String name;
    private String imageUrl;

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
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
