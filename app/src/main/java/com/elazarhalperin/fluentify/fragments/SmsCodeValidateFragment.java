package com.elazarhalperin.fluentify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.elazarhalperin.fluentify.R;

public class SmsCodeValidateFragment extends Fragment {

    LinearLayout ll_etHolder;
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

        setListeners();

    }

    private void setListeners() {
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

                }
            });
        }
    }
}