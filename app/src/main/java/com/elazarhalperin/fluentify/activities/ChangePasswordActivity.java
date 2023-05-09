package com.elazarhalperin.fluentify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.UserSignValidity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    TextInputEditText et_newPassword, et_confirmPassword, et_currentPasswrod;
    Button btn_changePassword;
    FloatingActionButton fab_goBack;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        et_newPassword = findViewById(R.id.et_newPassword);
        et_confirmPassword = findViewById(R.id.et_passwordConfirm);
        et_currentPasswrod = findViewById(R.id.et_currentPassword);

        btn_changePassword = findViewById(R.id.btn_changePassword);

        fab_goBack = findViewById(R.id.fab_goBack);

        user = FirebaseAuth.getInstance().getCurrentUser();

        setListeners();
    }

    private void setListeners() {
        // set click listener the go back button
        // when click the activity will be finished and the user will
        // to the previous activity.
        fab_goBack.setOnClickListener(v -> {
            finish();
        });

        btn_changePassword.setOnClickListener(v -> {
            changePassword();
        });

    }

    /**
     * the function will validate the fields and they are containing
     * in case some field isn't valid we will have error message in the invalid field and quit from the function.
     * in case all the fields are valid the user will proceed
     * to change the password.
     */
    private void changePassword() {
        // Get the string fields from the edittexts.
        String newPassword = et_newPassword.getText().toString().trim();
        String confirmPassword = et_newPassword.getText().toString().trim();
        String currentPassword = et_currentPasswrod.getText().toString().trim();

        // in the if statements if they are true
        // the user will go to the edittext and get an error
        // and qut from the function.

        // check if the current password is valid
        if (!UserSignValidity.isPasswordValid(currentPassword)) {
            et_newPassword.requestFocus();
            et_newPassword.setError("Invalid Password, pls try again.");
            return;
        }

        // check if the new password is valid
        if (!UserSignValidity.isPasswordValid(newPassword)) {
            et_newPassword.requestFocus();
            et_newPassword.setError("Invalid Password, pls try again.");
            return;
        }

        // check if the confirmed password is valid
        if (!newPassword.equals(confirmPassword)) {
            et_confirmPassword.requestFocus();
            et_confirmPassword.setError("The two fields must be identical!");
            return;
        }

        // check if the user typed correctly both of the password.
        if (newPassword.equals(currentPassword)) {
            et_newPassword.requestFocus();
            et_newPassword.setError("Can't change to the current password.");
            return;
        }

        verifyAndUpdatePassword(currentPassword, newPassword);
    }

    /**
     * @param current the current password the user have.
     * @param changed the password that the user want to update.
     * The function will update the user's password.
     */
    private void verifyAndUpdatePassword(String current, String changed) {
        // get the firebase authCredential, with the user email and currentPassword.
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), current);

        // to check if the user typed his current password
        // we use the reauthenticate the user, if the current password
        // is the real current password then we will update the password.
        user.reauthenticate(authCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // if the task is successful then we will try to update the passwrod.
                            user.updatePassword(changed)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Failed to update your password....", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to update your password....", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
