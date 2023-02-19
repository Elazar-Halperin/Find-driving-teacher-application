package com.elazarhalperin.fluentify.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.elazarhalperin.fluentify.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bnv_nav;
    NavController navController;



    @SuppressLint("MissingInflatedId") // for annoying unrelated errors
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bnv_nav = findViewById(R.id.bnv_homeNav);

        // get the navHost fragment and the navigation controller to use the navbar.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fcv_holder);
        navController = navHostFragment.getNavController();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // setting the navBar.
        NavigationUI.setupWithNavController(bnv_nav, navController);
    }
}