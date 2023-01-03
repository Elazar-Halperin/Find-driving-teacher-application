package com.elazarhalperin.fluentify.helpers;

import android.util.Patterns;

import java.util.regex.Pattern;

public class UserSignValidity {
    public static boolean isPasswordValid(String password) {
        if(password == null || password.isEmpty())
            return false;
        if (password.contains(" "))
            return false;
        if (password.length() < 8)
            return false;
        return true;
    }

    public static boolean isEmailPatternValid(String email) {
        if(email == null || email.isEmpty())
            return false;
        if(!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
            return false;
        }
        return true;
    }
}
