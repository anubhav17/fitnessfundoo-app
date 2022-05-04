package com.fitnessfundoo.model;

/**
 * Created by Anubhav on 25-03-2016.
 */
public class JoinedUser {
    private String user_id,title, thumbnailUrl;
    private String city,state,country;

    public JoinedUser() {
    }

    public JoinedUser(String user_id,String name, String thumbnailUrl, String city,String country,String state) {
        this.user_id = user_id;
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.city = city;
        this.state = state;
        this.country = country;
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


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}