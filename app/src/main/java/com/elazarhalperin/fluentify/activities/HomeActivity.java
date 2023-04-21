package com.elazarhalperin.fluentify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.icu.util.VersionInfo;
import android.os.Bundle;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.DarkModeManager;
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

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bnv_nav;
    NavController navController;

    static String userType;

    DarkModeManager darkModeManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();


        db.collection("teachers")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().getData() != null) {
                            userType = "teacher";
                        } else {
                            db.collection("students")
                                    .document(auth.getCurrentUser().getUid())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            userType = "student";
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