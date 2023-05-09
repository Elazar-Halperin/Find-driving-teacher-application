package com.elazarhalperin.fluentify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.DarkModeManager;
import com.elazarhalperin.fluentify.helpers.UserTypeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bnv_nav;
    NavController navController;


    UserTypeHelper userTypeHelper;

    DarkModeManager darkModeManager;

    SharedPreferences prefs;

    int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // set the current language.
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String lang = prefs.getString("lang", getResources().getConfiguration().locale.getLanguage());

        Locale locale = new Locale(lang);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        userTypeHelper = new UserTypeHelper(getApplicationContext());


        count = 0;

        bnv_nav = findViewById(R.id.bnv_homeNav);

        // get the navHost fragment and the navigation controller to use the navbar.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fcv_holder);
        navController = navHostFragment.getNavController();

        // Set the dark mode.
        darkModeManager = new DarkModeManager(getApplicationContext());
        if (darkModeManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        bnv_nav.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.learnFragment:
                        count++;
                        if(count >= 20) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://i.kym-cdn.com/entries/icons/original/000/036/244/kisscover.jpg")));
                            count = 0;
                        }
                }
            }
        });

        setUserType();

    }

    /**
     * the function will first check if the application have already user type.
     * if it has then we will quit from the function.
     * Otherwise we will go to the firebase with the current uid and will get the
     * user type in case of we don't have usertype.
     */
    private void setUserType() {
        if (!userTypeHelper.getUserType().isEmpty()) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        db.collection("teachers")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().getData() != null) {
                            userTypeHelper.setUserType("teacher");
                        } else {
                            db.collection("students")
                                    .document(auth.getCurrentUser().getUid())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            userTypeHelper.setUserType("student");
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // setting the navBar.
        NavigationUI.setupWithNavController(bnv_nav, navController);
    }
}