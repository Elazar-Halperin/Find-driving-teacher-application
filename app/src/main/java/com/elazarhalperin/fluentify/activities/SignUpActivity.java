package com.elazarhalperin.fluentify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.elazarhalperin.fluentify.Models.StudentModel;
import com.elazarhalperin.fluentify.Models.UserModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.UserSignValidity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText et_email, et_name, et_password, et_confirmPassword;
    Button btn_signUp, btn_transferToSignIn;

    LinearLayout ll_showProgressBar;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // initialize all the views.
        et_email = findViewById(R.id.et_emailSignUp);
        et_name = findViewById(R.id.et_nameSignUp);
        et_password = findViewById(R.id.et_passwordSignUp);
        et_confirmPassword = findViewById(R.id.et_passwordConfirm);

        btn_signUp = findViewById(R.id.btn_signUp);
        btn_transferToSignIn = findViewById(R.id.btn_goToSignIn);

        ll_showProgressBar = findViewById(R.id.ll_showProgressBar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setListeners();

    }

    private void setListeners() {
        btn_signUp.setOnClickListener(v -> {
            signUpUser();
        });

        btn_transferToSignIn.setOnClickListener( v-> {
            Intent toSignIn = new Intent(getApplicationContext(), SignInActivity.class);
            finish();
            startActivity(toSignIn);
        });
    }

    private void signUpUser() {
        String email = et_email.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String confirmPassword = et_confirmPassword.getText().toString().trim();

        if(!isValid(email, name, password, confirmPassword)) return;

        ll_showProgressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String uid = auth.getCurrentUser().getUid();
                StudentModel studentModel = new StudentModel(uid,name, email, "Januar 1", 0);
                db.collection("students")
                        .document(uid)
                        .set(studentModel)
                        .addOnCompleteListener(data -> {
                            if(data.isSuccessful()) {

                                Intent toHome = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(toHome);
                                Intent intent = new Intent();
                                intent.putExtra("key", "result");
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            } else {
                                auth.signOut();
                                Toast.makeText(getApplicationContext(), "Error accured while sigin up, Pls try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Error accured while sigin up, Pls try again", Toast.LENGTH_SHORT).show();
            }

            ll_showProgressBar.setVisibility(View.GONE);
        });


    }

    private boolean isValid(String email, String name, String password, String confirmPassword) {
        if(!UserSignValidity.isEmailPatternValid(email)) {
            et_email.requestFocus();
            et_email.setError("Please provide valid email!");
            return false;
        }

        if(name == null || name.isEmpty()) {
            et_name.requestFocus();
            et_name.setError("Please provide your name!");
            return false;
        }

        if(!UserSignValidity.isPasswordValid(password)) {
            et_password.requestFocus();
            et_password.setError("Please provide at least 8 characters!");
            return false;
        }
        if(!confirmPassword.equals(password)) {
            et_confirmPassword.requestFocus();
            et_confirmPassword.setError("Incorrect! Please type your password!");
            return false;
        }

        return true;
    }


}