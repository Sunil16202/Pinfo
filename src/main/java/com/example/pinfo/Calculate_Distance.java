package com.example.pinfo;

import java.util.ArrayList;

//https://stackoverflow.com/questions/6981916/how-to-calculate-distance-between-two-locations-using-their-longitude-and-latitu
public class Calculate_Distance {
    public boolean calc_distance(double lat_1, double long_1, double lat_2, double long_2,double allowed_distance) {
        double diff_long = long_1 - long_2;
        double distance = Math.sin(calc_deg2rad(lat_1))
                * Math.sin(calc_deg2rad(lat_2))
                + Math.cos(calc_deg2rad(diff_long))
                * Math.cos(calc_deg2rad(lat_1))
                * Math.cos(calc_deg2rad(lat_2));
        distance = Math.acos(distance);
        distance = calc_rad2deg(distance);
        distance = distance * 60 * 1.1515;
        boolean flag;
        if(allowed_distance>=distance){ flag=true; }
        else{ flag=false;}
        return (flag);
    }

    private double calc_deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double calc_rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    public ArrayList<Message> get_allPost(ArrayList<Message> allMessages, double latitude, double longitude){
        ArrayList<Message> newAllMessages= new ArrayList<Message>();
        Message temp_message= new Message();
        for(int i=0; i<allMessages.size(); i++){
            temp_message=allMessages.get(i);
            if(calc_distance(latitude,longitude,temp_message.getLatitude(),temp_message.getLongitude(),5)){
                newAllMessages.add(temp_message);
            }
        }
        if(newAllMessages.isEmpty()){
            Message default_msg=new Message("Sorry! No posts nearby!", "Welcome!");
            newAllMessages.add(default_msg);
        }
        return newAllMessages;
    }
}
