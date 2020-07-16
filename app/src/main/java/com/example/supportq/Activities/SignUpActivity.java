package com.example.supportq.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.supportq.R;
import com.example.supportq.Validator;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout etPassword;
    TextInputLayout etUsername;
    Button btnSignUp;

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
    }

    //listerner for sign up button
    public void signUpButtonClicked() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getEditText().getText().toString();
                String password = etPassword.getEditText().getText().toString();
                if (Validator.validateUser(etPassword, etUsername, password,username)) {
                    ParseUser user = new ParseUser();
                    user.setPassword(password);
                    user.setUsername(username);
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
                    Toast.makeText(SignUpActivity.this, "Username already exist, choose a different username", Toast.LENGTH_SHORT).show();
                    return;
                }
                etPassword.setVisibility(View.INVISIBLE);
                etUsername.setVisibility(View.INVISIBLE);
                goMainActivity();
            }
        });
    }

    public void goMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}