package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supportq.Models.ProgressIndicator;
import com.example.supportq.R;
import com.example.supportq.Validator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    private ImageView btnLogin;
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
    }

    public void setViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        username_input_text = findViewById(R.id.username_text_input);
        password_input_text = findViewById(R.id.password_text_input);
    }

    public void onEnterClicked(TextInputEditText textInputEditText) {
        textInputEditText.setOnEditorActionListener(new TextInputEditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    signInButtonClicked();
                    return true;
                }
                return false;
            }
        });
    }

    //listener for user sign in with existing credentials
    public void signInButtonClicked() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_input_text.getEditText().getText().toString();
                String password = password_input_text.getEditText().getText().toString();
                if (Validator.validateUser(password_input_text, username_input_text, password, username)) {
                    ProgressIndicator.showMessage(LoginActivity.this);
                    signInExistingUser(username, password);
                }
            }
        });
    }

    //existing user
    private void signInExistingUser(final String username, final String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    ProgressIndicator.hideMessage(LoginActivity.this);
                    username_input_text.setError("incorrect username");
                    password_input_text.setError("incorrect password");
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
                            Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                            btnLogin.setVisibility(View.INVISIBLE);
                        } else if (user == null) {
                            Toast.makeText(LoginActivity.this, "cancelled facebook login", Toast.LENGTH_SHORT).show();
                            btnLogin.setVisibility(View.VISIBLE);
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
                finish();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}