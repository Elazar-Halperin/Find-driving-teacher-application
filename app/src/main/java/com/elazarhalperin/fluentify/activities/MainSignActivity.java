package com.elazarhalperin.fluentify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.elazarhalperin.fluentify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainSignActivity extends AppCompatActivity {

    Button btn_transferToSignUp, btn_transferTOSignIn, btn_toTeacherSign;

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

        // set listener to views.
        setListeners();
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