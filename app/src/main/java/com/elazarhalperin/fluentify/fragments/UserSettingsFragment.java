package com.elazarhalperin.fluentify.fragments;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.os.ConfigurationCompat;


import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.ChangePasswordActivity;
import com.elazarhalperin.fluentify.activities.MainSignActivity;
import com.elazarhalperin.fluentify.helpers.DarkModeManager;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class UserSettingsFragment extends Fragment {
    TextView tv_editProfile, tv_changePassword, tv_language, tv_logOut, tv_appVersion;
    Switch switch_darkMode;

    RadioGroup rg_holder;

    RadioButton rb_english, rb_hebrew;

    LinearLayout linearLayout;

    FirebaseAuth auth;

    DarkModeManager darkModeManager;

    private SharedPreferences prefs;


    boolean isPressed;
    boolean isVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switch_darkMode = view.findViewById(R.id.darkModeSwitch);

        tv_editProfile = view.findViewById(R.id.tv_editProfile);
        tv_changePassword = view.findViewById(R.id.tv_changePassword);
        tv_language = view.findViewById(R.id.tv_language);
        tv_logOut = view.findViewById(R.id.tv_logOut);
        tv_appVersion = view.findViewById(R.id.tv_appVersion);

        rg_holder = view.findViewById(R.id.rg_holder);
        rb_english = view.findViewById(R.id.rb_english);
        rb_hebrew = view.findViewById(R.id.rb_hebrew);

        linearLayout = view.findViewById(R.id.layout);

        auth = FirebaseAuth.getInstance();

        darkModeManager = new DarkModeManager(getActivity());

        isPressed = false;
        isVisible = false;

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String lang = prefs.getString("lang", getActivity().getResources().getConfiguration().locale.getLanguage());

        if (lang.equals("en")) {
            rb_english.setChecked(true);
        } else {
            rb_hebrew.setChecked(true);
        }

        // smothing the transition when expanding the layout
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        signAppVersion();
        setCurrentMode();

        setListeners();
    }

    private void signAppVersion() {
        // Get the app's package information
        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Get the app's version name
        String appVersion = packageInfo.versionName;
        // Set the app's version to a TextView
        tv_appVersion.setText("App Version " + appVersion);
    }

    private void setCurrentMode() {
        boolean isDarkMode = darkModeManager.isDarkMode();

        switch_darkMode.setChecked(isDarkMode);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setListeners() {
        tv_editProfile.setOnClickListener(v -> {
            editProfile();
        });

        tv_changePassword.setOnClickListener(v -> {
            changePassword();
        });

        tv_language.setOnClickListener(v -> {
            changeLanguage();
        });


        tv_logOut.setOnClickListener(v -> {
            logOut();
        });

        switch_darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDarkMode(isChecked);
            }
        });

        rb_english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchAppLanguage("en");
                    Log.d("loch", "english");
                }
            }
        });
        rb_hebrew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchAppLanguage("he");
                    Log.d("loch", "hebrew");
                }
            }
        });

    }

    private void switchAppLanguage(String language) {
        // Save the user's language preference to SharedPreferences
        prefs.edit().putString("lang", language).apply();

        // Restart the activity to apply the new language
        recreate();
    }

    private void recreate() {
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

    private void setDarkMode(boolean isDarkMode) {
        darkModeManager.setDarkMode(isDarkMode);
        switch_darkMode.setChecked(isDarkMode);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void changeLanguage() {
        animateDrawable();
        expandRadioGroupAnimation();

        isPressed = !isPressed;
    }

    private void expandRadioGroupAnimation() {
        int visibility = !isPressed ? View.VISIBLE : View.GONE;

        TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
        rg_holder.setVisibility(visibility);
    }

    private void animateDrawable() {
        int MAX_LEVEL = 10000;

        Drawable[] myTextViewCompoundDrawables = tv_language.getCompoundDrawables();
        Drawable drawable = myTextViewCompoundDrawables[2]; // assuming the desired drawable is the last one

        if (drawable == null) {
            Toast.makeText(getActivity(), "its null", Toast.LENGTH_SHORT).show();
            return;
        }

        ObjectAnimator anim = ObjectAnimator.ofInt(drawable, "level", 0, MAX_LEVEL);

        if (!isPressed) {
            anim.start();
        } else {
            anim.reverse();
        }
    }

    private void editProfile() {
    }

    private void changePassword() {
        Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(i);
    }

    /**
     * First it will LogOut the user from the firebase, second it will close current activity.
     * lastly it will transfer the user into the MainSignActivity,
     * where the user can sign or create a user.
     */
    private void logOut() {
        auth.signOut();
        getActivity().finish();
        Intent i = new Intent(getActivity(), MainSignActivity.class);
        startActivity(i);
    }


}
