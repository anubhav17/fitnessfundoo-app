package com.fitnessfundoo.model;

import java.util.Date;

/**
 * Created by Anubhav on 07-02-2016.
 */
public class SportsEvent {

    private String id,title,address, thumbnailUrl,e_Stime,e_Etime,no_participants,description,
            contact_no,email,street,colony,city,state,country,pin,event_host_id,date_created,cat,number_visible,url,email_visible,distance;
    //private int year;
    private String event_date,lat,lon;
    //private ArrayList<String> genre;

    public SportsEvent() {
    }

    public SportsEvent(String id,String title,String e_Stime,String e_Etime,String no_participants,String description,String contact_no,String email,
                       String street,String colony,String city,String state,String country,String pin,String event_host_id,
                       String date_created,String cat,String number_visible,String lon,String thumbnailUrl,String address , String event_date,String lat,String url,String email_visible,String distance) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.address = address;
        this.event_date = event_date;
        this.lat = lat;
        this.pin = pin;
        this.event_host_id = event_host_id;
        this.date_created = date_created;
        this.cat = cat;
        this.number_visible = number_visible;
        this.lon = lon;
        this.e_Stime = e_Stime;
        this.e_Etime = e_Etime;
        this.no_participants = no_participants;
        this.description = description;
        this.contact_no = contact_no;
        this.email = email;
        this.street = street;
        this.colony = colony;
        this.city = city;
        this.state = state;
        this.country = country;
        this.url = url;
        this.email_visible = email_visible;
        this.distance = distance;
    }
    public String getNumberParticipant() {
        return no_participants;
    }

    public void setNumberParticipant(String no_participants) {
        this.no_participants = no_participants;
    }

    public String getEventId() {
        return id;
    }

    public void setEventId(String id) {
        this.id = id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public String getEmailId() {
        return email;
    }

    public void setEmailId(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getColony() {
        return colony;
    }

    public void setColony(String colony) {
        this.colony = colony;
    }



    public String getEmail_visible() {
        return email_visible;
    }

    public void setEmail_visible(String email_visible) {
        this.email_visible = email_visible;
    }


    public String getEvent_host_id() {
        return event_host_id;
    }

    public void setEvent_host_id(String eventHostId) {
        this.event_host_id = eventHostId;
    }


    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String dateCreated) {
        this.date_created = dateCreated;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }


    public String getNumber_visible() {
        return number_visible;
    }

    public void setNumber_visible(String numberVisible) {
        this.number_visible = numberVisible;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city_name) {
        this.city = city_name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state_name) {
        this.state = state_name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street_name) {
        this.street = street_name;
    }

    public String getEndTime() {
        return e_Etime;
    }

    public void setEndTime(String event_Etime) {
        this.e_Etime = event_Etime;
    }

    public String getStartTime() {
        return e_Stime;
    }

    public void setStartTime(String event_Stime) {
        this.e_Stime = event_Stime;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact) {
        this.contact_no = contact;
    }



    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
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

    public String getEventDate() {
        return event_date;
    }

    public void setEventDate(String event_date) {
        this.event_date = event_date;
    }


}

