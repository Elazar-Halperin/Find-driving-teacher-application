package com.elazarhalperin.fluentify.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
import com.elazarhalperin.fluentify.fragments.PhoneEntryFragment;
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
    NavController navController;
    static String sms_code = "";
    static String phone_number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_sign);
    }
}