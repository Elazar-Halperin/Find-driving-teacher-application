package com.elazarhalperin.fluentify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.Models.UserModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.UserSignValidity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TeacherSignActivity extends AppCompatActivity {

    TextInputEditText et_email, et_name, et_password, et_confirmPassword;
    Button btn_signUp, btn_transferToSignIn;

    LinearLayout ll_showProgressBar;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_sign);

        // initialize all the views and the objects;
        et_email = findViewById(R.id.et_emailSignUp);
        et_name = findViewById(R.id.et_nameSignUp);
        et_password = findViewById(R.id.et_passwordSignUp);
        et_confirmPassword = findViewById(R.id.et_passwordConfirm);

        btn_signUp = findViewById(R.id.btn_signUp);

        ll_showProgressBar = findViewById(R.id.ll_showProgressBar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // setting all the necessary listeners.
        setListeners();

    }

    /**
     * Function to set all the necessary views.
     */
    private void setListeners() {
        btn_signUp.setOnClickListener(v -> {
            signUpUser();
        });

    }

    /**
     * Function to sign the user up,
     * it will take all the text from the edittext.
     * after that it will check if the fields are vaild, if so then it will
     * sign the user up,
     * else it will give an error to the user.
     */
    private void signUpUser() {
        // getting all the text from the edittext.
        String email = et_email.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String confirmPassword = et_confirmPassword.getText().toString().trim();

        // checking if the fields are valid. if not then it will give an error and stop the signing up process.
        if(!isValid(email, name, password, confirmPassword)) return;

        // show the progressbar
        ll_showProgressBar.setVisibility(View.VISIBLE);

        // starts the authentication by using email and password.
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();
                        Random rand = new Random();
                        TeacherModel teacher = new TeacherModel(uid,name, email, "Januar 1", 140.00, rand.nextDouble() * 5, "nod", "nod", "nod", null);

                        // using map to add the teacher into the database,
                        // we using map because firestore is unable to detact the inheritance between teacher and user models, and without map it will add only
                        // user object which is not good.
                        Map<String, Object> user = new HashMap<>();
                        user = teacher.getMap();
                        // adding the teacher into the database .
                        db.collection("teachers")
                                .document(uid)
                                .set(user)
                                .addOnCompleteListener(data -> {
                                    if(data.isSuccessful()) {
                                        Log.d("tag",teacher.toString());
                                        Intent toHome = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(toHome);
                                        // this 3 lines of code will make the MainSignActivity close.
                                        Intent intent = new Intent();
                                        intent.putExtra("key", "result");
                                        setResult(Activity.RESULT_OK, intent);
                                        // the activity will close;
                                        finish();
                                    } else {
                                        // the adding teacher into the database failed so we want to start the process again
                                        // so we have to sign out from the authentication.
                                        auth.signOut();
                                        Toast.makeText(getApplicationContext(), "Error accured while sigin up, Pls try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Error accured while sigin up, Pls try again", Toast.LENGTH_SHORT).show();
                    }
                    // the authentication ended.
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