package com.example.supportq.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_ADDITIONAL_INFO = "info";
    public static final String KEY_IMAGE = "profilePicture";
    public static final String KEY_EVENT = "event";
    public static final String KEY_DATE = "startDate";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public void setEvent(String description) {
        put(KEY_EVENT, description);
    }

    public String getEvent() {
        return getString(KEY_EVENT);
    }

    public void setAdditionalInfo(String info) {
        put(KEY_ADDITIONAL_INFO, info);
    }

    public String getAdditionalInfo() {
        return getString(KEY_ADDITIONAL_INFO);
    }

    public void setStartDate(String date) {
        put(KEY_DATE, date);
    }

    public String getStartDate() {
        return getString(KEY_DATE);
    }

    public void setStartTime(String startTime) {
        put(KEY_START_TIME, startTime);
    }

    public String getStartTime() {
        return getString(KEY_START_TIME);
    }

    public void setEndTime(String endTime) {
        put(KEY_END_TIME, endTime);
    }

    public String getEndTime() {
        return getString(KEY_END_TIME);
    }
}
