package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.supportq.Activities.Instructor.InstructorMainActivity;
import com.example.supportq.Activities.Student.MainActivity;
import com.example.supportq.BuildConfig;
import com.example.supportq.Models.User;
import com.example.supportq.R;
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
    private TextInputLayout etUsername;
    private TextInputLayout etToken;
    private Button btnRegister;
    private String fullName;
    private boolean isInstructor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setViews();
        getFacebookInformation();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String secretToken = etToken.getEditText().getText().toString();
                if (!secretToken.isEmpty() && !secretToken.equals(BuildConfig.TOKEN)) {
                    etToken.setError(getString(R.string.SECRET_KEY));
                    return;
                } else if (secretToken.equals(BuildConfig.TOKEN)) {
                    isInstructor = true;
                }
                registerUser();
            }
        });
    }

    private void setViews(){
        etUsername = findViewById(R.id.etUsername);
        btnRegister = findViewById(R.id.btnRegister);
        etToken = findViewById(R.id.etToken);
    }

    private void registerUser() {
        String username = etUsername.getEditText().getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(this, getString(R.string.USERNAME_EMPTY_ERROR), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RegistrationActivity.this, getString(R.string.REGISTRATION_ERROR), Toast.LENGTH_SHORT).show();
                } else if (objects.size() != 0) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.USERNAME_TAKEN), Toast.LENGTH_SHORT).show();
                } else {
                    finishRegistration(ParseUser.getCurrentUser(), username);
                }
            }
        });
    }

    private void finishRegistration(ParseUser currentUser, String username) {
        currentUser.setUsername(username);
        User.setFullName(fullName, currentUser);
        if (isInstructor) {
            User.setIsInstructor(currentUser);
        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.TOAST_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isInstructor) {
                    Intent intent = new Intent(RegistrationActivity.this, InstructorMainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                }
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
                            fullName = object.getString(getString(R.string.name));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        request.executeAsync();
    }
}