package com.fitnessfundoo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anubhav on 25-03-2016.
 */
public class InterestLevel {
    private String sports_name;
    private String level;
    private int position;

    public InterestLevel() {
    }

    public InterestLevel(String sports_name,String level,int position) {
        this.sports_name = sports_name;
        this.position = position;
        this.level = level;
    }

    public String getSports_name() {
        return sports_name;
    }

    public void setSports_name(String sports_name) {
        this.sports_name = sports_name;
    }


    public int getSportsPosition() {
        PosotionSports posotionSports = new PosotionSports();
        return posotionSports.getPosition();
    }

    public void setSportsPosition(String sports_name,int position) {
        List<PosotionSports> s= new ArrayList<PosotionSports>();
        PosotionSports posotionSports = new PosotionSports(sports_name,position);
        posotionSports.setPositione(position);
        posotionSports.setSports_name(sports_name);
        s.add(0,posotionSports);
       // this.position = position;
    }


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

}