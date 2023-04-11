package com.elazarhalperin.fluentify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.elazarhalperin.fluentify.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;


public class searchFragment extends Fragment {
    FloatingActionButton fab_filter, fab_search;
    RecyclerView rv_teachers;
    ChipGroup cg_license, cg_preference;
    AutoCompleteTextView actv_cities;
    BottomSheetDialog dialog;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        fab_search = v.findViewById(R.id.fab_search);
        fab_filter = v.findViewById(R.id.fab_filter);
        rv_teachers = v.findViewById(R.id.rv_teachers);
        actv_cities = v.findViewById(R.id.actv_choices);

        db = FirebaseFirestore.getInstance();

        setListeners();

    }

    private void setListeners() {
        fab_filter.setOnClickListener(v -> {
            showDialog();
        });
        fab_search.setOnClickListener(v -> {
            if (actv_cities.getText() == null || actv_cities.getText().toString().isEmpty()) {
                Snackbar.make(getView(), "Please pick a city.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Query query = db.collection("teachers");
            query = query.whereArrayContains("location", actv_cities.getText().toString());
            // Iterating through the chips to see which one is triggered.
            String license = getChoosenChipString(cg_license);

            if(!license.isEmpty()) {
                query = query.whereArrayContains("licences", Arrays.asList(license));

            }

            String preference = getChoosenChipString(cg_preference);

            if(!preference.isEmpty()) {
                query = query.orderBy(preference, Query.Direction.ASCENDING);
            }

            Toast.makeText(getActivity(), query.toString(), Toast.LENGTH_SHORT).show();


        });


    }

    private String getChoosenChipString(ChipGroup chipGroup) {

        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);

            if (chip.isChecked()) {
                return chip.getText().toString();
            }
        }

        return "";
    }

    void showDialog() {
        if (dialog == null) {
            dialog = new BottomSheetDialog(getActivity(), R.style.DialogStyle);
            dialog.setContentView(R.layout.filter_layout);
        }

        cg_license = dialog.findViewById(R.id.cg_license);
        cg_preference = dialog.findViewById(R.id.cg_preference);

        for (int i = 0; i < cg_license.getChildCount(); i++) {
            Chip chip = (Chip) cg_license.getChildAt(i);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check whether the chips is filtered by user
                    // or not and give the suitable Toast message
                    if (chip.isChecked()) {
                        chip.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background_main));
                    }
                }
            });
        }

        dialog.show();
    }
}