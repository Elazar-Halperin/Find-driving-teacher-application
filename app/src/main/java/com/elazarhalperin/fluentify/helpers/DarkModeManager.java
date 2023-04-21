package com.elazarhalperin.fluentify.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class DarkModeManager {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;

    public static final String SP_NAME = "MainApplicationSP";
    public static final String DARK_MODE_BOOLEAN = "isDarkMOde";

    public DarkModeManager(Context context) {
        this.context = context;

        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void setDarkMode(boolean isDarkMode) {
        editor.putBoolean(DARK_MODE_BOOLEAN, isDarkMode);
        editor.commit();
    }

    public boolean isDarkMode() {
        return sp.getBoolean(DARK_MODE_BOOLEAN, false);
    }
}
