package com.example.supportq.Activities.Instructor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.supportq.BuildConfig;
import com.example.supportq.Fragments.MyAlertDialogFragment;
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
        setUpSignUpButtonListener();
    }

    private void setViews() {
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnSignUp = findViewById(R.id.btnSignUp);
        etFullname = findViewById(R.id.etFullname);
        etToken = findViewById(R.id.etToken);
    }

    //listerner for sign up button
    private void setUpSignUpButtonListener() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getEditText().getText().toString();
                String password = etPassword.getEditText().getText().toString();
                String fullName = etFullname.getEditText().getText().toString();
                String secretToken = etToken.getEditText().getText().toString();
                if(!secretToken.equals(BuildConfig.TOKEN)){
                    etToken.setError(getString(R.string.SECRET_KEY));
                    return;
                }
                etToken.setError(null);
                if (fullName.isEmpty() || fullName.length() < 4) {
                    etFullname.setError(String.valueOf(R.string.FULL_NAME_ERROR));
                    return;
                }
                etFullname.setError(null);
                if (Validator.validateUser(InstructorSignUpActivity.this, etPassword, etUsername, password, username)) {
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
                    Toast.makeText(InstructorSignUpActivity.this, getString(R.string.USERNAME_TAKEN), Toast.LENGTH_SHORT).show();
                    return;
                }
                goToInstructorMainActivity();
            }
        });
    }

    private void goToInstructorMainActivity() {
        Intent intent = new Intent(InstructorSignUpActivity.this, InstructorMainActivity.class);
        startActivity(intent);
        finish();
    }
}