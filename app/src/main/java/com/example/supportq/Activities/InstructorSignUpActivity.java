package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.supportq.BuildConfig;
import com.example.supportq.Models.InternetConnection;
import com.example.supportq.Models.ProgressIndicator;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.example.supportq.Validator;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class InstructorSignUpActivity extends AppCompatActivity {
    public static final String SECRET_KEY_MESSAGE = "wrong secret key";
    private TextInputLayout etPassword;
    private TextInputLayout etUsername;
    private TextInputLayout etFullname;
    private TextInputLayout etToken;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_sign_up);
        setViews();
        signUpButtonClicked();
    }

    private void setViews() {
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnSignUp = findViewById(R.id.btnSignUp);
        etFullname = findViewById(R.id.etFullname);
        etToken = findViewById(R.id.etToken);
    }

    //listerner for sign up button
    private void signUpButtonClicked() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getEditText().getText().toString();
                String password = etPassword.getEditText().getText().toString();
                String fullName = etFullname.getEditText().getText().toString();
                String secretToken = etToken.getEditText().getText().toString();
                if(!secretToken.equals(BuildConfig.TOKEN)){
                    etToken.setError(SECRET_KEY_MESSAGE);
                    return;
                }
                etToken.setError(null);
                if (fullName.isEmpty() || fullName.length() < 4) {
                    etFullname.setError(Validator.FULL_NAME_ERROR);
                    return;
                }
                etFullname.setError(null);
                if (Validator.validateUser(etPassword, etUsername, password, username)) {
                    ParseUser user = new ParseUser();
                    user.setPassword(password);
                    user.setUsername(username);
                    User.setFullName(fullName, user);
                    User.setIsInstructor(user);
                    if (InternetConnection.isNetworkConnected(InstructorSignUpActivity.this)) {
                        ProgressIndicator.showMessage(InstructorSignUpActivity.this);
                        signUp(username, password, user);
                    } else {
                        errorMessageOnEditText(null, null, null);
                        Toast.makeText(InstructorSignUpActivity.this, LoginActivity.NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }

    //error message to display on textInput
    private void errorMessageOnEditText(String fullname, String username, String password) {
        etFullname.setError(fullname);
        etUsername.setError(username);
        etPassword.setError(password);
    }

    private void signUp(final String username, final String password, final ParseUser user) {
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    ProgressIndicator.hideMessage(InstructorSignUpActivity.this);
                    Toast.makeText(InstructorSignUpActivity.this, RegistrationActivity.USERNAME_TAKEN, Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
                ProgressIndicator.hideMessage(InstructorSignUpActivity.this);
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(InstructorSignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}