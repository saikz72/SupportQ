package com.example.supportq;

import android.app.Application;

import com.example.supportq.Models.Question;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Register your parse models
        ParseObject.registerSubclass(Question.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("support-q") // should correspond to APP_ID env variable
                .clientKey("final-fbu-project")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://support-q.herokuapp.com/parse/").build());
        //TODO --> facebook sdk
        //ParseFacebookUtils.initialize(this);
        //FacebookSdk.sdkInitialize(getApplicationContext());

    }
}
