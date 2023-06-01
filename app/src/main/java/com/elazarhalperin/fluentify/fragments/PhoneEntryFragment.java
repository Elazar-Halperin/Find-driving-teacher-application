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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_phoneNumber = view.findViewById(R.id.et_phoneNumber);
        btn_sendCode = view.findViewById(R.id.btn_getCode);
        pb_buttonLoad = view.findViewById(R.id.pb_buttonLoad);

        // intializing navController to pass between fragments.
        navController = Navigation.findNavController(view);

        auth = FirebaseAuth.getInstance();

//        Toast.makeText(getActivity(), ccp_code.getSelectedCountryCode(), Toast.LENGTH_SHORT).show();



        btn_sendCode.setOnClickListener(v-> {
            btn_sendCode.setEnabled(false);
            pb_buttonLoad.setVisibility(View.VISIBLE);
            String phoneNumber = "+972" + et_phoneNumber.getText().toString().trim();
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(getActivity())                 // Activity (for callback binding)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    btn_sendCode.setEnabled(true);
                                    pb_buttonLoad.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Verification completed", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    btn_sendCode.setEnabled(true);
                                    pb_buttonLoad.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Verification failed", Toast.LENGTH_LONG).show();

                                    e.printStackTrace();
                                }



                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(verificationId, forceResendingToken);


                                    // get all teh values to the SmsCodeValidate fragment


                                    Bundle bundle = new Bundle();
                                    bundle.putString("verificationId", verificationId);
                                    bundle.putString("phoneNumber", phoneNumber);

//                                    SmsCodeValidateFragment smsCodeValidateFragment = new SmsCodeValidateFragment();
//                                    smsCodeValidateFragment.setArguments(bundle);

                                    navController.navigate(R.id.action_phoneEntryFragment_to_smsCodeValidateFragment2, bundle);
//                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fcv_navContainer, smsCodeValidateFragment).commit();

                                }
                            }).build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });

    }

}