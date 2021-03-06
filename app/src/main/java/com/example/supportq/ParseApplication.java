package com.example.supportq;

import android.app.Application;

import com.example.supportq.Models.Event;
import com.example.supportq.Models.Question;
import com.example.supportq.Models.Reply;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Register parse models
        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(Reply.class);
        ParseObject.registerSubclass(Event.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("support-q") // should correspond to APP_ID env variable
                .clientKey("final-fbu-project")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://support-q.herokuapp.com/parse/").build());
        ParseFacebookUtils.initialize(this);

    }
}
