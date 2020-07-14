package com.example.supportq.Models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Question")
public class Question extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKES = "likes";

    //sets the description of the question
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    //gets the description of the question
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    //sets the user of the question
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public Date getDate() {
        return this.getCreatedAt();
    }

    //returns the relative time of question
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    // Returns the array of users who liked this post.
    public JSONArray getLikes() {
        return getJSONArray(KEY_LIKES);
    }
    // Returns the number of likes on this post.
    public int getLikeCount() {
        if (getLikes() != null) {
            return getLikes().length();
        } else return 0;
    }

    // Add a like to this post.
    public void likePost(ParseUser user) {
        add(KEY_LIKES, user);
    }

    // Remove a like from this post.
    public void unlikePost(ParseUser user) {
        ArrayList<ParseUser> a = new ArrayList<>();
        a.add(user);
        removeAll(KEY_LIKES, a);
    }

    // Check if this post has been liked.
    public boolean isLiked() {
        JSONArray a = getLikes();
        if (a != null) {
            for (int i = 0; i < a.length(); i++) {
                try {
                    if (a.getJSONObject(i).getString("objectId").equals(ParseUser.getCurrentUser().getObjectId())) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
