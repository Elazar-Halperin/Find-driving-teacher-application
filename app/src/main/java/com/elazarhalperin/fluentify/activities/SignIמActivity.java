package com.elazarhalperin.fluentify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.elazarhalperin.fluentify.R;
import com.google.android.material.textfield.TextInputEditText;

public class SignIמActivity extends AppCompatActivity {

    Button btn_signIn, btn_transferToSignUp;
    TextInputEditText et_email, et_password;
    LinearLayout ll_showProgressBar;

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

        // set listeners to all desired views;
        setListeners();


    }

    private void setListeners() {
        Intent toSignUp = new Intent(getApplicationContext(), SignUpActivity.class);
        btn_signIn.setOnClickListener( v-> {
            Intent intent = new Intent();
            intent.putExtra("key", "result");
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        btn_transferToSignUp.setOnClickListener( v-> {
            finish();
            startActivity(toSignUp);
        });
    }
}