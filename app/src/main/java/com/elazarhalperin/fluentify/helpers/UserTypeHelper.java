package com.elazarhalperin.fluentify.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class UserTypeHelper {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;

    public static final String SP_NAME = "MainApplicationSP";
    public static final String USER_TYPE_STRING = "userType";

    public static final String TEACHER_TYPE = "teacher";
    public static final String STUDENT_TYPE = "student";


    public UserTypeHelper(@NonNull Context context) {
        this.context = context;

        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * the function removes the user type from the SharedPreferences
     * the function used when you logging out from the user in the application.
     */
    public void removeUserType() {
        editor.putString(USER_TYPE_STRING, "");
        editor.commit();
    }

    /**
     * @param userType string type that may contain only "student" or "teacher"
     * the function will put it in the SharedPreferences
     */
    public void setUserType(String userType) {
        editor.putString(USER_TYPE_STRING, userType);
        editor.commit();
    }

    /**
     * @return get the user type "student" or "teacher", if the SP doesn't have string it will return empty string.
     */
    public String getUserType() {
        return sp.getString(USER_TYPE_STRING, "");
    }

}
