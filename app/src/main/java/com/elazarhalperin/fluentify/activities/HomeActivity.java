package com.elazarhalperin.fluentify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.util.VersionInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.DarkModeManager;
import com.elazarhalperin.fluentify.helpers.UserTypeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bnv_nav;
    NavController navController;


    UserTypeHelper userTypeHelper;

    DarkModeManager darkModeManager;

    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String lang = prefs.getString("lang", getResources().getConfiguration().locale.getLanguage());

        Locale locale = new Locale(lang);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        userTypeHelper = new UserTypeHelper(getApplicationContext());


        bnv_nav = findViewById(R.id.bnv_homeNav);

        // get the navHost fragment and the navigation controller to use the navbar.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fcv_holder);
        navController = navHostFragment.getNavController();

        darkModeManager = new DarkModeManager(getApplicationContext());
        if(darkModeManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        getUserType();

    }

    private void getUserType() {
        if(!userTypeHelper.getUserType().isEmpty()) return;

        Log.d("lcoh", userTypeHelper.getUserType());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        db.collection("teachers")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getData() != null) {
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