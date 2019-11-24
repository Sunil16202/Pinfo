package com.example.pinfo;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class User_Details implements Serializable {
    private String gmail_name, //formal name
                   username,   // displayed to others
                   email,
                   owner_id,
                   post_collection;
    private Bitmap bmp_profile_image;
//    Map<String,String> my_interacted_posts = new HashMap<>();
//    ArrayList<String> my_post_list= new ArrayList<String>();

    public User_Details(String gmail_name, String username, String email, String owner_id, String post_collection, Bitmap bmp_profile_image) {
        this.gmail_name = gmail_name;
        this.username = username;
        this.email = email;
        this.owner_id = owner_id;
        this.post_collection = post_collection;
        this.bmp_profile_image = bmp_profile_image;
    }

}
