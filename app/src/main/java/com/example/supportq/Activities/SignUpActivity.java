package com.example.supportq.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.supportq.Models.ProgressIndicator;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.example.supportq.Validator;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout etPassword;
    private TextInputLayout etUsername;
    private TextInputLayout etFullname;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setViews();
        signUpButtonClicked();
    }

    public void setViews() {
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnSignUp = findViewById(R.id.btnSignUp);
        etFullname = findViewById(R.id.etFullname);
    }

    //listerner for sign up button
    public void signUpButtonClicked() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getEditText().getText().toString();
                String password = etPassword.getEditText().getText().toString();
                String fullName = etFullname.getEditText().getText().toString();
                if(fullName.isEmpty() || fullName.length() < 4){
                    etFullname.setError(Validator.FULL_NAME_ERROR);
                    return;
                }
                etFullname.setError(null);
                if (Validator.validateUser(etPassword, etUsername, password,username)) {
                    ParseUser user = new ParseUser();
                    user.setPassword(password);
                    user.setUsername(username);
                    User.setFullName(fullName, user);
                    ProgressIndicator.showMessage(SignUpActivity.this);
                    signUp(username, password, user);
                }
            }
        });
    }

    private void signUp(final String username, final String password, final ParseUser user) {
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    ProgressIndicator.hideMessage(SignUpActivity.this);
                    Toast.makeText(SignUpActivity.this, RegistrationActivity.USERNAME_TAKEN, Toast.LENGTH_SHORT).show();
                    return;
                }
                etPassword.setVisibility(View.INVISIBLE);
                etUsername.setVisibility(View.INVISIBLE);
                goMainActivity();
                ProgressIndicator.hideMessage(SignUpActivity.this);
            }
        });
    }

    public void goMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}