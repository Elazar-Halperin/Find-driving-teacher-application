package com.elazarhalperin.fluentify.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elazarhalperin.fluentify.Models.UserModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.HomeActivity;
import com.elazarhalperin.fluentify.helpers.UserSignValidity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FinalSignUpTeacherFragment extends Fragment {
    TextInputEditText et_email, et_password, et_confirmPassword;
    Button btn_signUp;

    LinearLayout ll_showProgressBar;

    FirebaseAuth auth;
    FirebaseFirestore db;


    String name, phoneNumber, info, locations;
    double lessonPrice;
    Uri selectedImage;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;

        name = bundle.getString("name");
        info = bundle.getString("info");
        locations = bundle.getString("locations");
        phoneNumber = bundle.getString("phoneNumber");
        lessonPrice = bundle.getDouble("lessonPrice");
        selectedImage = bundle.getParcelable("selectedImage");

        btn_signUp = view.findViewById(R.id.btn_signUp);
        et_email = view.findViewById(R.id.et_emailSignUp);
        et_password = view.findViewById(R.id.et_passwordSignUp);
        et_email = view.findViewById(R.id.et_passwordConfirm);

    }

    private void setListeners() {
        btn_signUp.setOnClickListener(v -> {
            signUpUser();
        });

    }

    private void signUpUser() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String confirmPassword = et_confirmPassword.getText().toString().trim();

        if(!isValid(email, password, confirmPassword)) return;

        ll_showProgressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();
                        UserModel user = new UserModel(uid,name, email, "Januar 1");
                        db.collection("Users")
                                .document(uid)
                                .set(user)
                                .addOnCompleteListener(data -> {
                                    if(data.isSuccessful()) {

                                        Intent toHome = new Intent(getActivity(), HomeActivity.class);
                                        startActivity(toHome);
                                        Intent intent = new Intent();
                                        intent.putExtra("key", "result");
                                        getActivity().setResult(Activity.RESULT_OK, intent);
                                        getActivity().finish();
                                    } else {
                                        auth.signOut();
                                        Toast.makeText(getActivity(), "Error accured while sigin up, Pls try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Error accured while sigin up, Pls try again", Toast.LENGTH_SHORT).show();
                    }

                    ll_showProgressBar.setVisibility(View.GONE);
                });


    }

    private boolean isValid(String email, String password, String confirmPassword) {
        if(!UserSignValidity.isEmailPatternValid(email)) {
            et_email.requestFocus();
            et_email.setError("Please provide valid email!");
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