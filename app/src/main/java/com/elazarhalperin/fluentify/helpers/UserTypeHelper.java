package com.elazarhalperin.fluentify.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class UserTypeHelper {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;

    public static final String SP_NAME = "MainApplicationSP";
    public static final String USER_TYPE_STRING = "userType";

    public static final String TEACHER_TYPE = "teacher";
    public static final String STUDENT_TYPE = "student";


    public UserTypeHelper(Context context) {
        this.context = context;

        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void removeUserType() {
        editor.putString(USER_TYPE_STRING, "");
        editor.commit();
    }

    public void setUserType(String userType) {
        editor.putString(USER_TYPE_STRING, userType);
        editor.commit();
    }

    public String getUserType() {
        return sp.getString(USER_TYPE_STRING, "");
    }

}
