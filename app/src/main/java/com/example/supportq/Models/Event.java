package com.example.supportq.Models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Locale;

@ParseClassName("Event")
public class Event extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_ADDITIONAL_INFO = "info";
    public static final String KEY_IMAGE = "profilePicture";
    public static final String KEY_EVENT = "event";
    public static final String KEY_DATE = "startDate";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";
    public static final String KEY_CREATED_AT = "createdAt";

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

    public String getCreatedTimeAgo() {
        return getRelativeTimeAgo(this.getCreatedAt().getTime());
    }

    public String getRelativeTimeAgo(long dateMillis) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return relativeDate;
    }
}
