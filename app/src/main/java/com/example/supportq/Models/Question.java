package com.example.supportq.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Question")
public class Question extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE =  "image";
    public static final String KEY_CREATED_AT = "createdAt";

    //sets the description of the question
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    //gets the description of the question
    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    //sets the user of the question
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    

}
