package com.example.supportq;

import com.google.android.material.textfield.TextInputLayout;

public class Validator {
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
        if(password.length() > 5)
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
            etPassword.setError("password cannot be empty");
            etUsername.setError(null);
            return false;
        }
        else if (!Validator.isPasswordLongEnough(password)) {
            etPassword.setError("password should be more than 5 characters");
            etUsername.setError(null);
            return false;
        } else if (Validator.isUsernameEmpty(username)) {
            etPassword.setError(null);
            etUsername.setError("username cannot be empty");
            return false;
        } else if (!Validator.isUsernameLongEnough(username)) {
            etPassword.setError(null);
            etUsername.setError("username should be more than 5 characters");
            return false;
        }

        return true;
    }
}
