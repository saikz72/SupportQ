package com.example.supportq.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.supportq.Activities.Instructor.InstructorMainActivity;
import com.example.supportq.Activities.Instructor.InstructorSignUpActivity;
import com.example.supportq.Activities.Student.MainActivity;
import com.example.supportq.Activities.Student.SignUpActivity;
import com.example.supportq.Fragments.MyAlertDialogFragment;
import com.example.supportq.Models.InternetConnection;
import com.example.supportq.Models.ProgressIndicator;
import com.example.supportq.Models.User;
import com.example.supportq.R;
import com.example.supportq.Validator;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private Button btnSignUp;
    private Button btnSignIn;
    private Button btnInstructor;
    private TextInputLayout username_input_text;
    private TextInputLayout password_input_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //allows for persistence
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && !User.getIsInstructor(currentUser)) {
            goToMainActivity();
        }else if(currentUser != null && User.getIsInstructor(currentUser)){
            goToInstructorMainActivity();
        }
        setViews();
        faceBookLogin();
        setUpSignUpButtonListenerForStudent();
        setUpSignButtonListenerForInstructor();
        setSignInButtonListener();
    }

    private void setViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnInstructor = findViewById(R.id.btnInstructor);
        username_input_text = findViewById(R.id.username_text_input);
        password_input_text = findViewById(R.id.password_text_input);
    }

    //listener for user sign in with existing credentials
    private void setSignInButtonListener() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_input_text.getEditText().getText().toString();
                String password = password_input_text.getEditText().getText().toString();
                if (Validator.validateUser(LoginActivity.this, password_input_text, username_input_text, password, username)) {
                    if (InternetConnection.isNetworkConnected(LoginActivity.this)) {
                        ProgressIndicator.showMessage(LoginActivity.this);
                        signInExistingUser(username, password);
                    } else {
                        errorMessageOnEditText(null, null);
                        showAlertDialog();
                        return;
                    }
                }
            }
        });
    }

    //no internet alertdialog
    private void showAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        MyAlertDialogFragment alertDialog = MyAlertDialogFragment.newInstance(getString(R.string.NO_INTERNET_MESSAGE));
        alertDialog.show(fm, getString(R.string.fragment_tag));
    }

    //error message to display on textInput
    private void errorMessageOnEditText(String usernameError, String passwordError) {
        username_input_text.setError(usernameError);
        password_input_text.setError(passwordError);
    }

    //message to send user when there is no internent
    private void showErrorMessageIfInternetMissing(ParseException e) {
        //connection fail
        if (e.getCode() == e.CONNECTION_FAILED || e.getCode() == e.OTHER_CAUSE) {
            errorMessageOnEditText(null, null);
            ProgressIndicator.hideMessage(LoginActivity.this);
        }
    }

    //message to send user when the credentials are invalid
    private void validateLoginCredentials(ParseException e) {
        //user doesn't exist
        if (e.getCode() == e.OBJECT_NOT_FOUND) {
            ProgressIndicator.hideMessage(LoginActivity.this);
            errorMessageOnEditText(getString(R.string.INCORRECT_USERNAME), getString(R.string.INCORRECT_PASSWORD));
        }
    }

    //existing user
    private void signInExistingUser(final String username, final String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    showErrorMessageIfInternetMissing(e);
                    validateLoginCredentials(e);
                    return;
                }
                if (User.getIsInstructor(user)) {
                    goToInstructorMainActivity();
                } else {
                    goToMainActivity();
                }
            }
        });
    }

    private void faceBookLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, null, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            showErrorMessageIfInternetMissing(e);
                            validateLoginCredentials(e);
                            showAlertDialog();
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

    private void setUpSignUpButtonListenerForStudent() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpSignButtonListenerForInstructor() {
        btnInstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, InstructorSignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToInstructorMainActivity() {
        Intent intent = new Intent(this, InstructorMainActivity.class);
        startActivity(intent);
        finish();
    }
}