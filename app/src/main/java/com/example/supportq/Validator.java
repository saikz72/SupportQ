package com.example.supportq;

import com.google.android.material.textfield.TextInputLayout;

public class Validator {
    public static final String PASSWORD_EMPTY_ERROR="password cannot be empty";
    public static final String PASSWORD_LENGTH_ERROR="password should be more than 5 characters";
    public static final String USERNAME_EMPTY_ERROR="username cannot be empty";
    public static final String USERNAME_LENGTH_ERROR="username should be more than 5 characters";
    public static final String FULL_NAME_ERROR = "full name should be more than 5 characters";
    public static boolean isUsernameEmpty(String name) {
        if (name.isEmpty())
            return true;
        return false;
    }

    public static boolean isUsernameLongEnough(String name){
        if(name.length() > 4)
            return true;
        return false;
    }

    public static boolean isPasswordLongEnough(String password){
        if(password.length() > 4)
            return true;
        return false;
    }

    public static boolean isPasswordEmpty(String password){
        if(password.isEmpty())
            return true;
        return false;
    }
    public static boolean validateUser(TextInputLayout etPassword, TextInputLayout etUsername, String password, String username) {
        if (Validator.isPasswordEmpty(password)) {
            etPassword.setError(PASSWORD_EMPTY_ERROR);
            etUsername.setError(null);
            return false;
        }
        else if (!Validator.isPasswordLongEnough(password)) {
            etPassword.setError(PASSWORD_LENGTH_ERROR);
            etUsername.setError(null);
            return false;
        } else if (Validator.isUsernameEmpty(username)) {
            etPassword.setError(null);
            etUsername.setError(USERNAME_EMPTY_ERROR);
            return false;
        } else if (!Validator.isUsernameLongEnough(username)) {
            etPassword.setError(null);
            etUsername.setError(USERNAME_LENGTH_ERROR);
            return false;
        }

        return true;
    }
}
