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
        fab_goBack.setOnClickListener(v -> {
            finish();
        });

        btn_changePassword.setOnClickListener(v -> {
            String newPassword = et_newPassword.getText().toString().trim();
            String confirmPassword = et_newPassword.getText().toString().trim();
            String currentPassword = et_currentPasswrod.getText().toString().trim();

            if (!UserSignValidity.isPasswordValid(currentPassword)) {
                et_newPassword.requestFocus();
                et_newPassword.setError("Invalid Password, pls try again.");
                return;
            }

            if (!UserSignValidity.isPasswordValid(newPassword)) {
                et_newPassword.requestFocus();
                et_newPassword.setError("Invalid Password, pls try again.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                et_confirmPassword.requestFocus();
                et_confirmPassword.setError("The two fields must be identical!");
                return;
            }

            if (newPassword.equals(currentPassword)) {
                et_newPassword.requestFocus();
                et_newPassword.setError("Can't change to the current password.");
                return;
            }

            verifyAndUpdatePassword(currentPassword, newPassword);
        });

    }

    private void verifyAndUpdatePassword(String current, String changed) {

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), current);


        user.reauthenticate(authCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
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
