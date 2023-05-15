package com.elazarhalperin.fluentify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.elazarhalperin.fluentify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainSignActivity extends AppCompatActivity {

    Button btn_transferToSignUp, btn_transferTOSignIn, btn_toTeacherSign;

    ImageView imageView;

    int REQUEST_SIGN = 1;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sign);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // checks if the user is signed in.
        if(firebaseUser != null) {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            finish();
            startActivity(i);
        }


        // initialize values.
        btn_transferTOSignIn = findViewById(R.id.btn_transfterToSignIn);
        btn_transferToSignUp = findViewById(R.id.btn_transfterToSignUp);
        btn_toTeacherSign = findViewById(R.id.btn_goToTeacherSignUP);

        imageView = findViewById(R.id.imageView);


        // set listener to views.
        setListeners();
        checkForSmsReceivePermissions();

    }

    void checkForSmsReceivePermissions(){
        // Check if App already has permissions for receiving SMS
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.RECEIVE_SMS") == PackageManager.PERMISSION_GRANTED) {
            // App has permissions to listen incoming SMS messages
            Log.d("adnan", "checkForSmsReceivePermissions: Allowed");
        } else {
            // App don't have permissions to listen incoming SMS messages
            Log.d("adnan", "checkForSmsReceivePermissions: Denied");

            // Request permissions from user
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECEIVE_SMS}, 43391);
        }
    }

    private void setListeners() {
        btn_transferToSignUp.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivityForResult(i, REQUEST_SIGN);
        });

        btn_transferTOSignIn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SignInActivity.class);
            startActivityForResult(i, REQUEST_SIGN);
        });

        btn_toTeacherSign.setOnClickListener(v-> {
            Intent i = new Intent(getApplicationContext(), TeacherSignActivity.class);
            startActivityForResult(i, REQUEST_SIGN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SIGN && resultCode == Activity.RESULT_OK) {
            finish();

        }

    }
}