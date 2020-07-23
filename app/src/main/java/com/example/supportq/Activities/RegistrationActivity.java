package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.example.supportq.Validator;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity {
    public static final String REGISTRATION_ERROR = "Registration error";
    public static final String USERNAME_TAKEN = "Username is already taken";
    private static final String name = "name";
    private TextInputLayout etUsername;
    private Button btnRegister;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getFacebookInformation();
        etUsername = findViewById(R.id.etUsername);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = etUsername.getEditText().getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(this, Validator.USERNAME_EMPTY_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }
        validateUserName(username);
    }

    private void validateUserName(final String username) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(User.KEY_USERNAME, username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(RegistrationActivity.this, REGISTRATION_ERROR, Toast.LENGTH_SHORT).show();
                } else if (objects.size() != 0) {
                    Toast.makeText(RegistrationActivity.this, USERNAME_TAKEN, Toast.LENGTH_SHORT).show();
                } else
                    finishRegistration(ParseUser.getCurrentUser(), username);
            }
        });
    }

    private void finishRegistration(ParseUser currentUser, String username) {
        currentUser.setUsername(username);
        User.setFullName(fullName, currentUser);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(RegistrationActivity.this, LoginActivity.TOAST_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //gets user's full name and profile picture from Facebook
    private void getFacebookInformation() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            fullName = object.getString(name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        request.executeAsync();
    }
}