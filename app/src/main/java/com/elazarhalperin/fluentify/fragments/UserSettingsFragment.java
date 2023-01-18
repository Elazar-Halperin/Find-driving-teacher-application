package com.elazarhalperin.fluentify.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.SettingsActivity;

public class UserSettingsFragment extends Fragment {
    ImageButton ib_toSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_settings, container, false);

        ib_toSettings = view.findViewById(R.id.ib_toSettings);

        ib_toSettings.setOnClickListener( v-> {
            Intent i  = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        });

        return view;
    }
}