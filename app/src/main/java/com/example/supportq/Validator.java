package com.example.supportq;

import android.content.Context;

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
        if(password.length() > 4)
            return true;
        return false;
    }

    public static boolean isPasswordEmpty(String password){
        if(password.isEmpty())
            return true;
        return false;
    }
    public static boolean validateUser(Context context, TextInputLayout etPassword, TextInputLayout etUsername, String password, String username) {
        if (Validator.isPasswordEmpty(password)) {
            etPassword.setError(context.getString(R.string.PASSWORD_EMPTY_ERROR));
            etUsername.setError(null);
            return false;
        }
        else if (!Validator.isPasswordLongEnough(password)) {
            etPassword.setError(context.getString(R.string.PASSWORD_LENGTH_ERROR));
            etUsername.setError(null);
            return false;
        } else if (Validator.isUsernameEmpty(username)) {
            etPassword.setError(null);
            etUsername.setError(context.getString(R.string.USERNAME_EMPTY_ERROR));
            return false;
        } else if (!Validator.isUsernameLongEnough(username)) {
            etPassword.setError(null);
            etUsername.setError(context.getString(R.string.USERNAME_LENGTH_ERROR));
            return false;
        }

        return true;
    }
}
