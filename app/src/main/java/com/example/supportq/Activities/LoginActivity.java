package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.supportq.Models.ProgressIndicator;
import com.example.supportq.R;
import com.example.supportq.Validator;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String INCORRECT_USERNAME = "incorrect username";
    public static final String INCORRECT_PASSWORD = "incorrect password";
    public static final String TOAST_ERROR_MESSAGE = "something went wrong";
    public static final String NO_INTERNET_MESSAGE = "No Internet Connection";
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnSignIn;
    private TextInputLayout username_input_text;
    private TextInputLayout password_input_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //allows for persistence
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }
        setViews();
        faceBookLogin();
        SignUpButtonListener();
        signInButtonClicked();
    }

    public void setViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        username_input_text = findViewById(R.id.username_text_input);
        password_input_text = findViewById(R.id.password_text_input);
    }

    //listener for user sign in with existing credentials
    public void signInButtonClicked() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_input_text.getEditText().getText().toString();
                String password = password_input_text.getEditText().getText().toString();
                if (Validator.validateUser(password_input_text, username_input_text, password, username)) {
                    if (isNetworkConnected()) {
                        ProgressIndicator.showMessage(LoginActivity.this);
                        signInExistingUser(username, password);
                    } else {
                        errorMessageOnEditText(null, null);
                        Toast.makeText(LoginActivity.this, NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    //error message to display on textInput
    public void errorMessageOnEditText(String usernameError, String passwordError){
        username_input_text.setError(usernameError);
        password_input_text.setError(passwordError);
    }

    //message to send user when there is no internent
    public void internetAvailable(ParseException e) {
        //connection fail
        if (e.getCode() == e.CONNECTION_FAILED || e.getCode() == e.OTHER_CAUSE) {
            errorMessageOnEditText(null, null);
            Toast.makeText(LoginActivity.this, NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
            ProgressIndicator.hideMessage(LoginActivity.this);
        }
    }

    //message to send user when the credentials are invalid
    public void validateLoginCredentials(ParseException e) {
        //user doesn't exist
        if (e.getCode() == e.OBJECT_NOT_FOUND) {
            ProgressIndicator.hideMessage(LoginActivity.this);
            errorMessageOnEditText(INCORRECT_USERNAME, INCORRECT_PASSWORD);
        }
    }

    //existing user
    private void signInExistingUser(final String username, final String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    internetAvailable(e);
                    validateLoginCredentials(e);
                    return;
                }
                goToMainActivity();
                ProgressIndicator.hideMessage(LoginActivity.this);
            }
        });
    }

    public void faceBookLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, null, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            internetAvailable(e);
                            validateLoginCredentials(e);
                            return;
                        }
                        logUser(user);
                    }
                });
            }
        });
    }

    private void logUser(ParseUser user) {
        if (user == null) {
            return;
        }
        if (user.isNew()) {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void SignUpButtonListener() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}