package com.example.supportq.Models;

import com.parse.ParseUser;

public class User {
    public static String KEY_USERNAME = "username";
    public static String KEY_PROFILE_PICTURE_ID = "profilePictureId";
    public static String KEY_FULL_NAME = "fullName";

    // Returns the user's full name.
    public static String getFullName(ParseUser user) {
        return user.getString(KEY_FULL_NAME);
    }

    // Set the user's full name.
    public static void setFullName(String fullName, ParseUser user) {
        user.put(KEY_FULL_NAME, fullName);
    }

    public static void setProfilePicture(String id, ParseUser user) {
        user.put(KEY_PROFILE_PICTURE_ID, id);
    }

    public static String getProfilePicture(ParseUser user) {
        return user.getString(KEY_PROFILE_PICTURE_ID);
    }

    public static String getUserName(ParseUser user){
        return user.getUsername();
    }

    public static void setUserName(ParseUser user, String userName){
        user.put(KEY_USERNAME,userName);
    }
}
