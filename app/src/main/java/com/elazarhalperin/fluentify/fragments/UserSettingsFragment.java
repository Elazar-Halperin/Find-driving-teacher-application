package com.elazarhalperin.fluentify.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.MainSignActivity;
import com.elazarhalperin.fluentify.activities.SettingsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserSettingsFragment extends Fragment {
    ImageButton ib_toSettings;
    Button btn_signOut;
    TextView tv_userName, tv_joinDate;

    String name;
    String joinDate;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ib_toSettings = view.findViewById(R.id.ib_toSettings);

        btn_signOut = view.findViewById(R.id.btn_signOut);

        tv_joinDate = view.findViewById(R.id.tv_joinDate);
        tv_userName = view.findViewById(R.id.tv_profileName);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        name = "";
        joinDate = "";

        ib_toSettings.setOnClickListener( v-> {
            Intent i  = new Intent(getActivity(), SettingsActivity.class);
            startActivity(i);
        });

        btn_signOut.setOnClickListener(v -> {
            auth.signOut();
            Intent toSign = new Intent(getActivity(), MainSignActivity.class);
            getActivity().finish();
            startActivity(toSign);

        });

        fillTheFields();
    }

    private void fillTheFields() {
        db.collection("teachers").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        setTeacherFields(documentSnapshot);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        db.collection("students").document(firebaseUser.getUid())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        setStudentFields(documentSnapshot);
                                    }
                                });
                    }
                });
    }

    private void setStudentFields(DocumentSnapshot documentSnapshot) {

    }

    private void setTeacherFields(DocumentSnapshot documentSnapshot) {
    }
}