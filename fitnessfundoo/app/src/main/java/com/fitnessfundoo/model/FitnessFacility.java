package com.fitnessfundoo.model;

/**
 * Created by Anubhav on 04-02-2016.
 */


public class FitnessFacility  {
    private String title,address,contact,city,lon, thumbnailUrl,lat;
    //private int year;
    private double rating;
    private String id;
    private String sunOpen,parking,lockerShower,freeTrial,persTraining,open_time,close_time,url,distance;
    //private ArrayList<String> genre;

    public FitnessFacility() {
    }

    public FitnessFacility(String id,String title, String thumbnailUrl,String address , double rating,String lat,String lon,
                           String sunOpen,String parking,String lockerShower,String freeTrial,String persTraining,String open_time,String close_time,String url,String distance) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.address = address;
        this.rating = rating;
        this.lat = lat;
        this.lon = lon;
        this.sunOpen = sunOpen;
        this.parking = parking;
        this.lockerShower = lockerShower;
        this.freeTrial = freeTrial;
        this.persTraining = persTraining;
        this.open_time = open_time;
        this.close_time = close_time;
        this.url = url;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSunOpen() {
        return sunOpen;
    }

    public void setSunOpen(String sunOpen) {
        this.sunOpen = sunOpen;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getFreeTrial() {
        return freeTrial;
    }

    public void setFreeTrial(String freeTrial) {
        this.freeTrial = freeTrial;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getLockerShower() {
        return lockerShower;
    }

    public void setLockerShower(String lockerShower) {
        this.lockerShower = lockerShower;
    }


    public String getPersTraining() {
        return persTraining;
    }

    public void setPersTraining(String persTraining) {
        this.persTraining = persTraining;
    }

}
