package com.elazarhalperin.fluentify.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.Models.UserModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.HomeActivity;
import com.elazarhalperin.fluentify.helpers.UserSignValidity;
import com.elazarhalperin.fluentify.helpers.UserTypeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class FinalSignUpTeacherFragment extends Fragment {
    TextInputEditText et_email, et_password, et_confirmPassword;
    Button btn_signUp;

    LinearLayout ll_showProgressBar;

    FirebaseAuth auth;
    FirebaseFirestore db;
    StorageReference storage;


    String name, phoneNumber, info, locations;
    List<String> licenses;
    double lessonPrice;
    Uri selectedImage;

    UserTypeHelper userTypeHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_final_sign_up_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the bundle from the arguments
        Bundle bundle = getArguments();

        // Retrieve the values from the bundle
        name = bundle.getString("name");
        info = bundle.getString("info");
        locations = bundle.getString("locations");
        phoneNumber = bundle.getString("phoneNumber");
        lessonPrice = bundle.getDouble("lessonPrice");
        selectedImage = bundle.getParcelable("selectedImage");
        licenses = bundle.getStringArrayList("licenses");

        // Display a Toast message with the retrieved name
        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();

        // Find the views in the layout
        ll_showProgressBar = view.findViewById(R.id.ll_showProgressBar);
        btn_signUp = view.findViewById(R.id.btn_signUp);
        et_email = view.findViewById(R.id.et_emailSignUp);
        et_password = view.findViewById(R.id.et_passwordSignUp);
        et_confirmPassword = view.findViewById(R.id.et_passwordConfirm);

        // Initialize Firebase components
        storage = FirebaseStorage.getInstance().getReference("profile_pictures");
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Create a UserTypeHelper instance
        userTypeHelper = new UserTypeHelper(getActivity());

        // Set listeners for the views
        setListeners();
    }

    private void setListeners() {
        btn_signUp.setOnClickListener(v -> {
            signUpUser();
        });

    }


    /**
     * Signs up a new user with the provided email and password.
     * Creates a new teacher profile and stores it in the database.
     * Uploads the selected profile image to the storage.
     * Redirects the user to the home screen upon successful signup.
     */
    private void signUpUser() {
        String email = et_email.getText().toString().trim(); // Retrieve the email entered by the user
        String password = et_password.getText().toString().trim(); // Retrieve the password entered by the user
        String confirmPassword = et_confirmPassword.getText().toString().trim(); // Retrieve the confirmed password entered by the user

        if (!isValid(email, password, confirmPassword)) {
            return; // If the entered data is not valid, return without signing up the user
        }

        ll_showProgressBar.setVisibility(View.VISIBLE); // Show progress bar while signing up

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid(); // Get the user ID of the newly created user

                        // Get the current date in the US locale
                        Locale locale = new Locale("en", "US");
                        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                        String date = dateFormat.format(new Date());

                        // Get the current date in the Hebrew locale
                        Locale he_locale = new Locale("he", "IL");
                        dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, he_locale);
                        String he_date = dateFormat.format(new Date());

                        // Create a new TeacherModel instance with the user data
                        TeacherModel user = new TeacherModel(uid, name, email, date, he_date, lessonPrice, locations, info, phoneNumber, licenses);
                        Map<String, Object> teacher = user.getMap(); // Get the data map from the TeacherModel instance

                        // Store the teacher profile data in the "teachers" collection of the Firestore database
                        db.collection("teachers")
                                .document(uid)
                                .set(teacher)
                                .addOnCompleteListener(data -> {
                                    if (data.isSuccessful()) {
                                        // Upload the selected profile image to the storage
                                        storage.child("profile_image" + uid + ".jpg")
                                                .putFile(selectedImage)
                                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            userTypeHelper.setUserType(UserTypeHelper.TEACHER_TYPE); // Set the user type to "Teacher"

                                                            // Redirect the user to the home screen
                                                            Intent toHome = new Intent(getActivity(), HomeActivity.class);
                                                            startActivity(toHome);
                                                            ll_showProgressBar.setVisibility(View.GONE); // Hide the progress bar
                                                            Intent intent = new Intent();
                                                            toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.putExtra("key", "result");
                                                            getActivity().setResult(Activity.RESULT_OK, intent);
                                                            getActivity().finish();
                                                        }
                                                    }
                                                });
                                    } else {
                                        auth.signOut(); // Sign out the user if there was an error during signup
                                        ll_showProgressBar.setVisibility(View.GONE); // Hide the progress bar
                                        Toast.makeText(getActivity(), "Error occurred while signing up, please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        ll_showProgressBar.setVisibility(View.GONE); // Hide the progress bar
                        Toast.makeText(getActivity(), "Error occurred while signing up, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Validates the email and password fields.
     *
     * @param email            The email address to validate.
     * @param password         The password to validate.
     * @param confirmPassword The confirmation password to compare with the original password.
     * @return True if the email and password are valid; false otherwise.
     */
    private boolean isValid(String email, String password, String confirmPassword) {
        if (!UserSignValidity.isEmailPatternValid(email)) {
            et_email.requestFocus();
            et_email.setError("Please provide a valid email!");
            return false;
        }

        if (!UserSignValidity.isPasswordValid(password)) {
            et_password.requestFocus();
            et_password.setError("Please provide at least 8 characters for the password!");
            return false;
        }

        if (!confirmPassword.equals(password)) {
            et_confirmPassword.requestFocus();
            et_confirmPassword.setError("The confirmation password does not match!");
            return false;
        }

        return true;
    }

}