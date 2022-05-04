package com.fitnessfundoo.model;

/**
 * Created by Anubhav on 16-04-2016.
 */
public class PosotionSports {

    private String sports_name;
    private int position;

    public PosotionSports(){};

    public PosotionSports(String sports_name,int position){
        this.sports_name = sports_name;
        this.position = position;
    }


    public String getSports_name() {
        return sports_name;
    }

    public void setSports_name(String sports_name) {
        this.sports_name = sports_name;
    }

    public int getPosition() {
        return position;
    }

    public void setPositione(int position) {
        this.position = position;
    }

}
