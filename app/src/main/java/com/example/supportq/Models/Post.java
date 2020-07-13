package com.example.supportq.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE =  "image";

    //sets the description of the post
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    //gets the description of the post
    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    //sets the user of the post
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    //gets the image of the post
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    //sets the image of the post
    public  void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }
}
