package com.elazarhalperin.fluentify.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
import android.icu.util.HebrewCalendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.HomeActivity;
import com.elazarhalperin.fluentify.broadcast_receivers.SmsReceiverBroadcastReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class SmsCodeValidateFragment extends Fragment implements SmsReceiverBroadcastReceiver.SmsVerificationCallback {

    LinearLayout ll_etHolder;
    Button btn_confirmSms;
    ProgressBar pb_buttonLoad;
    String verificationId, phoneNumber;

    NavController navController;

    SmsReceiverBroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize and register your BroadcastReceiver
        broadcastReceiver = new SmsReceiverBroadcastReceiver();

        // Initialize the callback so we can get the code from the sms.
        broadcastReceiver.setCallback(this);

        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        getContext().registerReceiver(broadcastReceiver, filter, Manifest.permission.RECEIVE_SMS, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sms_code_validate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ll_etHolder = view.findViewById(R.id.ll_edittextHolder);
        btn_confirmSms = view.findViewById(R.id.btn_confrimSms);
        pb_buttonLoad = view.findViewById(R.id.pb_buttonLoad);

        navController = Navigation.findNavController(view);

        Bundle bundle = getArguments();
        verificationId = bundle.getString("verificationId");
        phoneNumber = bundle.getString("phoneNumber");

        setListeners();


    }

    private void setListeners() {
        // assign to each editext that holds one number of the code
        // a listener that will detect if the user have typed
        // if he has than he will move to the next .
        for(int position = 0; position < ll_etHolder.getChildCount(); position++) {
            EditText editText = (EditText) ll_etHolder.getChildAt(position);
            int finalPosition = position;
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String text = charSequence.toString();

                    if(text.isEmpty() && finalPosition != 0) {
                        EditText et_before = (EditText) ll_etHolder.getChildAt(finalPosition - 1);
                        et_before.requestFocus();
                    }
                    if(!text.isEmpty() && finalPosition < ll_etHolder.getChildCount() - 1){
                        EditText et_after = (EditText) ll_etHolder.getChildAt(finalPosition + 1);
                        et_after.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(editable.length() == 0 && finalPosition != 0) {
                        EditText et_before = (EditText) ll_etHolder.getChildAt(finalPosition - 1);
                        et_before.requestFocus();
                    }
                    if(editable.length() != 0 && finalPosition < ll_etHolder.getChildCount() - 1){
                        EditText et_after = (EditText) ll_etHolder.getChildAt(finalPosition + 1);
                        et_after.requestFocus();
                    }
                }
            });
        }

        // clicked confirm
        // starts to check if the code is correct.
        btn_confirmSms.setOnClickListener(v-> {
            btn_confirmSms.setEnabled(false);
            pb_buttonLoad.setVisibility(View.VISIBLE);
            verificateCode();
        });

    }

    private void verificateCode() {
        String typedSms = getTypedSmsCodeFromUser();
        if(typedSms.isEmpty()) return;

        if(verificationId != null) {
            btn_confirmSms.setEnabled(false);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, typedSms);

            // this code does not actually signIn the user!
            // it only verifies the verification code!!!!
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                // I just want to check if the user is typed the right code.
                                // without it will create another user, which I don't want.
                                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "well done you verified your phone number", Toast.LENGTH_SHORT).show();

                                            Bundle bundle = new Bundle();
                                            bundle.putString("phoneNumber", phoneNumber);

                                            // move to the next fragment after all is right
                                            navController.navigate(R.id.action_smsCodeValidateFragment_to_extraTeacherDataFragment, bundle);

                                        }

                                        // return to normal
                                        pb_buttonLoad.setVisibility(View.GONE);
                                        btn_confirmSms.setEnabled(true);
                                    }
                                });

                            }

                        }
                    });
        }
    }

    // gets all the code from the 6 editTexts
    // so we can verificate if the code is the right.
    private String getTypedSmsCodeFromUser() {
        StringBuilder smsCode = new StringBuilder("");
        for(int i = 0; i < ll_etHolder.getChildCount(); i++) {
            EditText editText = (EditText) ll_etHolder.getChildAt(i);
            String text = editText.getText().toString().trim();

            if(text.isEmpty()) return "";

            smsCode.append(text);
        }

        return smsCode.toString();
    }

    /**
     * this function will trigger when sms code is sent
     * and the broadcast receiver is triggered
     * then we will get to this function.
     * This function will automatically verificate the sms code that was sent
     * the user won't have to add the code by himself.
     * @param verificationCode the sms code that was sent to us via firebase
     */
    @Override
    public void onVerificationCodeReceived(String verificationCode) {
        for(int i = 0; i < ll_etHolder.getChildCount(); i++) {
            EditText editText = (EditText) ll_etHolder.getChildAt(i);
            editText.setText(String.valueOf(verificationCode.charAt(i)));
        }
        if(verificationId != null) {
            btn_confirmSms.setEnabled(false);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, verificationCode);

            // this code does not actually signIn the user!
            // it only verifies the verification code!!!!
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                // I just wan't to check if the user is typed the right code.
                                // without it will create another user, which I don't want.
                                mAuth.getCurrentUser().delete();

                                Toast.makeText(getActivity(), "well done you verified your phone number", Toast.LENGTH_SHORT).show();
//                                Intent toHome = new Intent(getActivity(), HomeActivity.class);
//                                startActivity(toHome);
//                                Intent intent = new Intent();
//                                intent.putExtra("key", "result");
//                                getActivity().setResult(Activity.RESULT_OK, intent);

                                Bundle bundle = new Bundle();
                                bundle.putString("phoneNumber", phoneNumber);


                                // move to the next fragment.
                                navController.navigate(R.id.action_smsCodeValidateFragment_to_extraTeacherDataFragment, bundle);

//                                getActivity().finish();
                            }

                            pb_buttonLoad.setVisibility(View.GONE);
                            btn_confirmSms.setEnabled(true);

                        }
                    });
        }    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister your BroadcastReceiver
        getContext().unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }
}