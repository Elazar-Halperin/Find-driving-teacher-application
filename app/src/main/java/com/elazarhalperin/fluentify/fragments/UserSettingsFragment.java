package com.elazarhalperin.fluentify.fragments;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.elazarhalperin.fluentify.Models.StudentModel;
import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.Models.UserModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.ChangePasswordActivity;
import com.elazarhalperin.fluentify.activities.EditProfileActivity;
import com.elazarhalperin.fluentify.activities.MainSignActivity;
import com.elazarhalperin.fluentify.helpers.DarkModeManager;
import com.elazarhalperin.fluentify.helpers.UserTypeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserSettingsFragment extends Fragment {
    TextView tv_editProfile, tv_changePassword, tv_language, tv_logOut, tv_appVersion, tv_userName, tv_signUpDate, tv_finishedLessons;

    Button btn_minus, btn_plus;

    ImageView iv_profileImage;

    Switch switch_darkMode;

    RadioGroup rg_holder;

    LinearLayout ll_studentContainer, ll_progressBar;

    RadioButton rb_english, rb_hebrew;

    LinearLayout linearLayout;

    FirebaseAuth auth;

    DarkModeManager darkModeManager;

    private SharedPreferences prefs;
    UserTypeHelper userTypeHelper;

    Intent toEditProfileIntent;

    FirebaseUser firebaseUser;

    int finishedLessons;
    int finalFinishedLessons;

    boolean isPressed;
    boolean isVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        switch_darkMode = view.findViewById(R.id.darkModeSwitch);

        iv_profileImage = view.findViewById(R.id.iv_profileImage);

        tv_editProfile = view.findViewById(R.id.tv_editProfile);
        tv_changePassword = view.findViewById(R.id.tv_changePassword);
        tv_language = view.findViewById(R.id.tv_language);
        tv_logOut = view.findViewById(R.id.tv_logOut);
        tv_appVersion = view.findViewById(R.id.tv_appVersion);
        tv_userName = view.findViewById(R.id.tv_userName);
        tv_signUpDate = view.findViewById(R.id.tv_signUpDate);
        tv_finishedLessons = view.findViewById(R.id.tv_finishedLessons);

        ll_studentContainer = view.findViewById(R.id.ll_studentContainer);
        ll_progressBar = view.findViewById(R.id.ll_progressBar);

        btn_minus = view.findViewById(R.id.btn_minus);
        btn_plus = view.findViewById(R.id.btn_plus);

        rg_holder = view.findViewById(R.id.rg_holder);
        rb_english = view.findViewById(R.id.rb_english);
        rb_hebrew = view.findViewById(R.id.rb_hebrew);

        linearLayout = view.findViewById(R.id.layout);

        auth = FirebaseAuth.getInstance();

        toEditProfileIntent = new Intent(getActivity(), EditProfileActivity.class);

        darkModeManager = new DarkModeManager(getActivity());
        userTypeHelper = new UserTypeHelper(getActivity());

        finishedLessons = 0;
        finalFinishedLessons = 0;

        isPressed = false;
        isVisible = false;

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());


        String lang = prefs.getString("lang", getActivity().getResources().getConfiguration().locale.getLanguage());

        if (lang.equals("en")) {
            rb_english.setChecked(true);
        } else {
            rb_hebrew.setChecked(true);
            btn_minus.setRotationY(180f);
            btn_plus.setRotation(180f);

        }
        // content starts loading.
        ll_progressBar.setVisibility(View.VISIBLE);

        // smothing the transition when expanding the layout
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        if (!userTypeHelper.getUserType().equals(UserTypeHelper.STUDENT_TYPE)) {
            ll_studentContainer.setVisibility(View.GONE);
        } else {
            ll_studentContainer.setVisibility(View.VISIBLE);
        }

        signAppVersion();
        setCurrentMode();
        getUsersDataFromFirebase();

        setListeners();
    }

    private void signAppVersion() {
        // Get the app's package information
        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Get the app's version name
        String appVersion = packageInfo.versionName;
        // Set the app's version to a TextView
        tv_appVersion.setText("App Version " + appVersion);
    }

    private void setCurrentMode() {
        boolean isDarkMode = darkModeManager.isDarkMode();

        switch_darkMode.setChecked(isDarkMode);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setListeners() {
        tv_editProfile.setOnClickListener(v -> {
            editProfile();
        });

        tv_changePassword.setOnClickListener(v -> {
            changePassword();
        });

        tv_language.setOnClickListener(v -> {
            changeLanguage();
        });


        tv_logOut.setOnClickListener(v -> {
            logOut();
        });

        switch_darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDarkMode(isChecked);
            }
        });

        rb_english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchAppLanguage("en");
                    Log.d("loch", "english");
                }
            }
        });
        rb_hebrew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchAppLanguage("he");
                    Log.d("loch", "hebrew");
                }
            }
        });

        btn_plus.setOnClickListener(v -> {
            finishedLessons++;
            tv_finishedLessons.setText(finishedLessons + "");
        });

        btn_minus.setOnClickListener(v -> {
            if (finishedLessons <= 0) return;

            finishedLessons--;
            tv_finishedLessons.setText(finishedLessons + "");
        });

    }

    private void switchAppLanguage(String language) {
        // Save the user's language preference to SharedPreferences
        prefs.edit().putString("lang", language).apply();

        // Restart the activity to apply the new language
        recreate();
    }

    private void recreate() {
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

    private void setDarkMode(boolean isDarkMode) {
        darkModeManager.setDarkMode(isDarkMode);
        switch_darkMode.setChecked(isDarkMode);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void changeLanguage() {
        animateDrawable();
        expandRadioGroupAnimation();

        isPressed = !isPressed;
    }

    private void expandRadioGroupAnimation() {
        int visibility = !isPressed ? View.VISIBLE : View.GONE;

        TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
        rg_holder.setVisibility(visibility);
    }

    private void animateDrawable() {
        int MAX_LEVEL = 10000;

        Drawable[] myTextViewCompoundDrawables = tv_language.getCompoundDrawables();
        Drawable drawable = myTextViewCompoundDrawables[2]; // assuming the desired drawable is the last one

        if (drawable == null) {
            Toast.makeText(getActivity(), "its null", Toast.LENGTH_SHORT).show();
            return;
        }

        ObjectAnimator anim = ObjectAnimator.ofInt(drawable, "level", 0, MAX_LEVEL);

        if (!isPressed) {
            anim.start();
        } else {
            anim.reverse();
        }
    }

    private void editProfile() {
        Intent i = new Intent(getActivity(), EditProfileActivity.class);

        startActivity(i);
    }

    private void changePassword() {
        Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(i);
    }

    /**
     * First it will LogOut the user from the firebase, second it will close current activity.
     * lastly it will transfer the user into the MainSignActivity,
     * where the user can sign or create a user.
     */
    private void logOut() {
        auth.signOut();
        userTypeHelper.removeUserType();

        getActivity().finish();
        Intent i = new Intent(getActivity(), MainSignActivity.class);
        startActivity(i);
    }

    private void getUsersDataFromFirebase() {
        if (userTypeHelper.getUserType().isEmpty()) {
            Toast.makeText(getActivity(), "Error occured, pls try again.", Toast.LENGTH_SHORT).show();
        }
        String collection = userTypeHelper.getUserType().equals(UserTypeHelper.TEACHER_TYPE) ? "teachers" : "students";
        DocumentReference userRef = FirebaseFirestore.getInstance().collection(collection).document(firebaseUser.getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    assignDefaultUserFields(userModel);
                    toEditProfileIntent.putExtra("userModel", userModel);

                    if (collection.equals("teachers")) {
                        TeacherModel teacherModel = new TeacherModel(task.getResult().getData());
                        toEditProfileIntent.putExtra("teacherModel", teacherModel);
                        Log.d("teacherModel", teacherModel.toString());

                    } else {
                        StudentModel studentModel = new StudentModel(task.getResult().getData());
                        Log.d("studentModel", studentModel.toString());
                        getStudentData(studentModel);

                    }
                } else {

                }
            }
        });
    }

    private void getStudentData(StudentModel studentModel) {
        finalFinishedLessons = finishedLessons = studentModel.getFinishedLessons();

        tv_finishedLessons.setText(finishedLessons + "");
    }

    private void assignDefaultUserFields(UserModel userModel) {
        String name = userModel.getName();
        String date = userModel.getSignUpDate();

        tv_userName.setText(name);
        tv_signUpDate.setText(date);

        assignUserProfile();
    }

    private void assignUserProfile() {
        // get the reference of the storage where the profile image is stored.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profileImageRef = storage.getReference("profile_pictures").child("profile_image" + firebaseUser.getUid() + ".jpg");

        long MEGABYTE = 1024 * 1024;

        profileImageRef.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_profileImage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iv_profileImage.setImageDrawable(getActivity().getDrawable(R.drawable.proxy));
            }
        }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                ll_progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        // checks if the finished lessons has been changed,
        // if it isn't then we will not update in firebase
        if (finalFinishedLessons == finishedLessons) return;

        DocumentReference studentRef = FirebaseFirestore.getInstance().collection("students")
                .document(firebaseUser.getUid());

        studentRef.update("finishedLessons", finishedLessons)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Error occurred, your finished lessons couldn't be saved.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
