package com.example.supportq.Models;

import android.text.format.DateUtils;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ParseClassName("Question")
@Parcel(analyze = Question.class)
public class Question extends ParseObject implements Shrinkable, Comparable<Question> {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_IMAGE = "image";     //image of a question
    public static final String KEY_REPLIES = "replies";
    public static final String KEY_IS_DELETED = "isDeleted";
    public static final String KEY_HIDDEN_BY_USERS = "hiddenByUsers";
    public static final String KEY_BOOKMARKS = "bookmarks";
    private boolean isShrink = true;

    public void bookMarkQuestion(ParseUser user) {
        add(KEY_BOOKMARKS, user);
    }

    public JSONArray getBookMarkedQuestions() {
        return getJSONArray(KEY_BOOKMARKS);
    }

    public void unBookMarkQuestion(ParseUser user) {
        ArrayList<ParseUser> list = new ArrayList<>();
        list.add(user);
        removeAll(KEY_BOOKMARKS, list);
    }

    // Check if this post has been liked.
    public boolean isBookMarked() {
        JSONArray a = getBookMarkedQuestions();
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

    public void setQuestionToHidden(ParseUser user) {
        add(KEY_HIDDEN_BY_USERS, user);
    }

    public JSONArray getHiddenQuestion() {
        return getJSONArray(KEY_HIDDEN_BY_USERS);
    }

    public boolean didUserHideQuestion() {
        JSONArray jsonArray = getHiddenQuestion();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    if (jsonArray.getJSONObject(i).getString("objectId").equals(ParseUser.getCurrentUser().getObjectId())) {
                        return true;
                    }
                } catch (JSONException e) {
                    Log.e("Question", "didUserHideQuestion", e);
                }
            }
        }
        return false;
    }

    //remove user from the list of users that hid this question
    public void setQuestionVisibleToUser(ParseUser user) {
        ArrayList<ParseUser> list = new ArrayList<>();
        list.add(user);
        removeAll(KEY_HIDDEN_BY_USERS, list);
    }

    public void setIsDeleted(boolean isDeleted) {
        put(KEY_IS_DELETED, isDeleted);
    }

    public boolean getIsDeleted() {
        return getBoolean(KEY_IS_DELETED);
    }

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

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public void setReplies(List<Reply> replies) {
        put(KEY_REPLIES, replies);
    }

    public JSONArray getReplies() {
        return getJSONArray(KEY_REPLIES);
    }

    //Returns the number of replies on this post
    public int getRepliesCount() {
        int count = 0;
        JSONArray jsonArray = getReplies();
        if (jsonArray == null)
            return 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            count++;
        }
        return count;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(long dateMillis) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        return relativeDate;
    }

    public String getCreatedTimeAgo() {
        return getRelativeTimeAgo(this.getCreatedAt().getTime());
    }

    @Override
    public boolean isShrink() {
        return isShrink;
    }

    @Override
    public void setShrink(boolean isShrink) {
        this.isShrink = isShrink;
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

    @Override
    public int compareTo(Question question) {
        Integer likeCountOnThis = this.getLikeCount();
        Integer likeCountOnQuestion = question.getLikeCount();
        int priority = likeCountOnThis.compareTo(likeCountOnQuestion);      //if priority == +ve, this ^priority && if priority == -ve, question ^priority
        if (priority != 0) {
            return priority;
        } else {
            Date dateOnThis = this.getDate();
            Date dateOnQuestion = question.getDate();
            return dateOnThis.compareTo(dateOnQuestion);
        }
    }
}
