package com.fitnessfundoo.model;

import java.util.ArrayList;

/**
 * Created by Anubhav on 20-02-2016.
 */
public class RatingReview {
    private String title, thumbnailUrl,review,user_id;
    private double rating;

    public RatingReview() {
    }

    public RatingReview(String user_id,String name, String thumbnailUrl, double rating,String review) {
        this.user_id = user_id;
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.rating = rating;
        this.review = review;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

}