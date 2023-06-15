package com.elazarhalperin.fluentify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.UserSignValidity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    Button btn_signIn, btn_transferToSignUp;
    TextInputEditText et_email, et_password;
    LinearLayout ll_showProgressBar;

    FirebaseAuth auth;
    // just to fix things

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing);

        // initialize all the views
        btn_signIn = findViewById(R.id.btn_signIn);
        btn_transferToSignUp = findViewById(R.id.btn_goToSignUp);

        et_email = findViewById(R.id.et_emailSignIn);
        et_password = findViewById(R.id.et_passwordSignIn);

        ll_showProgressBar = findViewById(R.id.ll_showProgressBar);

        auth = FirebaseAuth.getInstance();

        // set listeners to all desired views;
        setListeners();


    }

    private void setListeners() {
        btn_signIn.setOnClickListener( v-> {

            signInUser();

        });

        btn_transferToSignUp.setOnClickListener( v-> {
            Intent toSignUp = new Intent(getApplicationContext(), SignUpActivity.class);
            finish();
            startActivity(toSignUp);
        });
    }

    private void signInUser() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        // check if the email and the password are valid.
        if(!isValid(email, password)) return;

        ll_showProgressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        // Signed in successfully
                        // move to the homeActivity and finish all activities that open.
                        Intent toHome = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(toHome);
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("key", "result");
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error accured while sigin up, Pls try again", Toast.LENGTH_SHORT).show();
                    }
                    ll_showProgressBar.setVisibility(View.GONE);
                });
    }

    /**
     * Validates the email and password for user sign-in or sign-up.
     *
     * @param email    The email to validate.
     * @param password The password to validate.
     * @return {@code true} if the email and password are valid, {@code false} otherwise.
     */
    private boolean isValid(String email, String password) {
        // Validate email pattern
        if (!UserSignValidity.isEmailPatternValid(email)) {
            et_email.requestFocus();
            et_email.setError("Please provide a valid email!");
            return false;
        }

        // Validate password length
        if (!UserSignValidity.isPasswordValid(password)) {
            et_password.requestFocus();
            et_password.setError("Please provide at least 8 characters!");
            return false;
        }

        return true;
    }
}