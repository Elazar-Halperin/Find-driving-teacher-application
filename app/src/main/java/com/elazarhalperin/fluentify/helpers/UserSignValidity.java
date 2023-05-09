package com.elazarhalperin.fluentify.helpers;

import android.util.Patterns;

import java.util.regex.Pattern;

public abstract class UserSignValidity {

    /**
     * @param password string
     * the function checks if the password is valid or not
     * the password has to be at least 8 characters and mustn't contain spaces
     * @return true if the password is valid else false.
     */
    public static boolean isPasswordValid(String password) {
        if(password == null || password.isEmpty())
            return false;
        if (password.contains(" "))
            return false;
        if (password.length() < 8)
            return false;
        return true;
    }

    /**
     * @param email string type
     * checks the validity of the email, by using the pattern function.
     * @return true if the email is valid.
     */
    public static boolean isEmailPatternValid(String email) {
        if(email == null || email.isEmpty())
            return false;
        if(!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
            return false;
        }
        return true;
    }
}
