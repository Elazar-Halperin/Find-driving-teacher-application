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
import com.elazarhalperin.fluentify.helpers.UserTypeHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText et_email, et_name, et_password, et_confirmPassword;
    Button btn_signUp, btn_transferToSignIn;

    LinearLayout ll_showProgressBar;

    FirebaseAuth auth;
    FirebaseFirestore db;

    UserTypeHelper userTypeHelper;

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

        // Create an instance of the helper class

        userTypeHelper = new UserTypeHelper(getApplicationContext());

        setListeners();

    }

    private void setListeners() {
        btn_signUp.setOnClickListener(v -> {
            signUpUser();
        });

        btn_transferToSignIn.setOnClickListener( v-> {
            // Transfer to the sign-in activity
            Intent toSignIn = new Intent(getApplicationContext(), SignInActivity.class);
            finish();
            startActivity(toSignIn);
        });
    }

    // Method to sign up a user
    private void signUpUser() {
        // Get user input values
        String email = et_email.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String confirmPassword = et_confirmPassword.getText().toString().trim();

        // Validate input values
        if (!isValid(email, name, password, confirmPassword)) return;

        // Show progress bar
        ll_showProgressBar.setVisibility(View.VISIBLE);

        // Create user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the unique user ID
                        String uid = auth.getCurrentUser().getUid();

                        Locale locale = new Locale("en", "US");
                        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                        String date = dateFormat.format(new Date());

                         Locale he_locale = new Locale("he", "IL");
                         dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, he_locale);
                        String he_date = dateFormat.format(new Date());


                        // Create a student model with user details
                        StudentModel studentModel = new StudentModel(uid, name, email, date, he_date, 0);

                        // Save student model to Firestore database
                        db.collection("students")
                                .document(uid)
                                .set(studentModel)
                                .addOnCompleteListener(data -> {
                                    if (data.isSuccessful()) {
                                        // Transfer to the home activity
                                        // and finish all activities.
                                        Intent toHome = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(toHome);

                                        // Set result and user type in the helper class
                                        Intent intent = new Intent();
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("key", "result");
                                        userTypeHelper.setUserType(UserTypeHelper.STUDENT_TYPE);
                                        setResult(Activity.RESULT_OK, intent);

                                        // Finish the sign-up activity
                                        finish();
                                    } else {
                                        // Sign out user and show error message
                                        auth.signOut();
                                        Toast.makeText(getApplicationContext(), "An error occurred while signing up. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Show error message
                        Toast.makeText(getApplicationContext(), "An error occurred while signing up. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    // Hide progress bar
                    ll_showProgressBar.setVisibility(View.GONE);
                });
    }


    /**
     * Validates the email, name, password, and confirm password fields for user sign-up.
     *
     * @param email           The email to validate.
     * @param name            The name to validate.
     * @param password        The password to validate.
     * @param confirmPassword The confirm password to validate.
     * @return {@code true} if the fields are valid, {@code false} otherwise.
     */
    private boolean isValid(String email, String name, String password, String confirmPassword) {
        // Validate email pattern
        if (!UserSignValidity.isEmailPatternValid(email)) {
            et_email.requestFocus();
            et_email.setError("Please provide a valid email!");
            return false;
        }

        // Validate name
        if (name == null || name.isEmpty()) {
            et_name.requestFocus();
            et_name.setError("Please provide your name!");
            return false;
        }

        // Validate password length
        if (!UserSignValidity.isPasswordValid(password)) {
            et_password.requestFocus();
            et_password.setError("Please provide at least 8 characters!");
            return false;
        }

        // Validate password and confirm password match
        if (!confirmPassword.equals(password)) {
            et_confirmPassword.requestFocus();
            et_confirmPassword.setError("Incorrect! Please type your password again!");
            return false;
        }

        return true;
    }

}