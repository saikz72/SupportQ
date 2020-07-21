package com.example.supportq.Models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Locale;

@ParseClassName("Reply")
public class Reply extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_APPROVED = "isApproved";
    public static final String KEY_QUESTION_ID = "questionID";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_REPLY_TEXT = "replyText";

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public boolean getIsApproved() {
        return getBoolean(KEY_APPROVED);
    }

    public void setIsApproved(boolean isApproved) {
        put(KEY_APPROVED, isApproved);
    }

    public void setQuestionId(Question questionId) {
        put(KEY_QUESTION_ID, questionId);
    }

    public Question getQuestionId() {
        return (Question) getParseObject(KEY_QUESTION_ID);
    }

    public void setReply(String reply) {
        put(KEY_REPLY_TEXT, reply);
    }

    public String getReply() {
        return getString(KEY_REPLY_TEXT);
    }

    public String getCreatedTimeAgo() {
        return getRelativeTimeAgo(this.getCreatedAt().getTime());
    }

    public String getRelativeTimeAgo(long dateMillis) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return relativeDate;
    }
}
