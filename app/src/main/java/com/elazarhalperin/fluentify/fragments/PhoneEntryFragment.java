package com.elazarhalperin.fluentify.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.TaskExecutor;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.elazarhalperin.fluentify.R;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;


public class PhoneEntryFragment extends Fragment {
    Button btn_sendCode;
    EditText et_phoneNumber;
    ProgressBar pb_buttonLoad;
    NavController navController;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_entry, container, false);
    }

    /**
     * Called when the fragment's view has been created.
     *
     * @param view               The View object returned by the inflater.
     * @param savedInstanceState A Bundle containing the saved state of the fragment, or null if there is no saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        et_phoneNumber = view.findViewById(R.id.et_phoneNumber);
        btn_sendCode = view.findViewById(R.id.btn_getCode);
        pb_buttonLoad = view.findViewById(R.id.pb_buttonLoad);

        // Initializing navController to navigate between fragments.
        navController = Navigation.findNavController(view);

        // Get FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Set click listener for send code button
        btn_sendCode.setOnClickListener(v -> {
            // Disable button and show progress bar while processing
            btn_sendCode.setEnabled(false);
            pb_buttonLoad.setVisibility(View.VISIBLE);

            // Get phone number from input field
            String phoneNumber = "+972" + et_phoneNumber.getText().toString().trim();

            // Set up PhoneAuthOptions for phone number verification
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(getActivity())                 // Activity (for callback binding)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    // Enable button and hide progress bar
                                    btn_sendCode.setEnabled(true);
                                    pb_buttonLoad.setVisibility(View.GONE);

                                    // Display verification completed message
                                    Toast.makeText(getActivity(), "Verification completed", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    // Enable button and hide progress bar
                                    btn_sendCode.setEnabled(true);
                                    pb_buttonLoad.setVisibility(View.GONE);

                                    // Display verification failed message
                                    Toast.makeText(getActivity(), "Verification failed", Toast.LENGTH_LONG).show();

                                    // Print exception stack trace
                                    e.printStackTrace();
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(verificationId, forceResendingToken);

                                    // Create bundle to pass verification ID and phone number to next fragment
                                    Bundle bundle = new Bundle();
                                    bundle.putString("verificationId", verificationId);
                                    bundle.putString("phoneNumber", phoneNumber);

                                    // Navigate to the next fragment (SmsCodeValidateFragment) with the bundle
                                    navController.navigate(R.id.action_phoneEntryFragment_to_smsCodeValidateFragment2, bundle);
                                }
                            }).build();

            // Start phone number verification
            PhoneAuthProvider.verifyPhoneNumber(options);
        });
    }

}