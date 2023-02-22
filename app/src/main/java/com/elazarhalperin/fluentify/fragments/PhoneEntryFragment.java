package com.elazarhalperin.fluentify.fragments;

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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.elazarhalperin.fluentify.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;


public class PhoneEntryFragment extends Fragment {
    CountryCodePicker ccp_code;
    Button btn_sendCode;
    EditText et_phoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ccp_code = view.findViewById(R.id.countryCodeHolder);
        et_phoneNumber = view.findViewById(R.id.et_phoneNumber);
        btn_sendCode = view.findViewById(R.id.btn_getCode);

//        Toast.makeText(getActivity(), ccp_code.getSelectedCountryCode(), Toast.LENGTH_SHORT).show();

        Typeface typeFace = ResourcesCompat.getFont(getActivity(), R.font.feather_bold);

        ccp_code.getTextView_selectedCountry().setTypeface(typeFace);

        ccp_code.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                makeTheFlagWithRoundedCorners();
            }
        });


        btn_sendCode.setOnClickListener(v-> {
            Toast.makeText(getActivity(), "+" + ccp_code.getSelectedCountryCode() + et_phoneNumber.getText().toString().trim(), Toast.LENGTH_SHORT).show();

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+" + ccp_code.getSelectedCountryCode() + et_phoneNumber.getText().toString().trim(),
                    60,
                    TimeUnit.SECONDS,
                    getActivity(),
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            btn_sendCode.setEnabled(true);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            btn_sendCode.setEnabled(true);
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(verificationId, forceResendingToken);
                            btn_sendCode.setEnabled(false);

                        }
                    }
            );
        });

        makeTheFlagWithRoundedCorners();
    }

    private void makeTheFlagWithRoundedCorners() {
        ImageView imageView = ccp_code.getImageViewFlag();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // create a new Bitmap object for the output
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        // create a new Canvas object to draw on the output bitmap
        Canvas canvas = new Canvas(output);

        // create a new Paint object with anti-aliasing enabled
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);

        // create a new Rect object to define the bounds of the output bitmap
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // create a new RectF object with the same bounds as the Rect object
        RectF rectF = new RectF(rect);

        // calculate the radius of the corners based on the size of the bitmap
        float radius = 10f;

        // draw a round rect shape with the specified radius and paint
        canvas.drawRoundRect(rectF, radius, radius, paint);
//        canvas.drawRoundRect()

        // set the Xfermode to SRC_IN to only draw pixels that are inside the round rect
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // draw the input bitmap onto the output bitmap
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // set the output bitmap as the background of the ImageView
        imageView.setImageBitmap(output);
    }
}