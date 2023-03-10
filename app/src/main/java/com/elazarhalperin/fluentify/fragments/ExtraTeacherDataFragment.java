package com.elazarhalperin.fluentify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elazarhalperin.fluentify.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class ExtraTeacherDataFragment extends Fragment {
    ChipGroup cg_choose, cg_pickedLicenses;
    List<String> pickedLicenses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_extra_teacher_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cg_choose = view.findViewById(R.id.cg_choose);
        cg_pickedLicenses = view.findViewById(R.id.cg_pickedLicense);

        List<String> licenses = new ArrayList<>();
        licenses.add("A");
        licenses.add("A1");
        licenses.add("B");
        licenses.add("C");
        licenses.add("C1");
        licenses.add("E");
        licenses.add("D");
        licenses.add("D1");
        licenses.add("D2");
        licenses.add("D3");

        for (String licens : licenses)
            generateChip(licens);
    }

    private void generateChip(String license) {
        Chip chip = new Chip(getActivity());
        chip.setText(license);
        chip.setChipIcon(
                getResources().getDrawable(R.drawable.ic_launcher_foreground, getActivity().getTheme())
        );
        chip.setChipIconVisible(false);
        chip.setClickable(true);
        // chip.setCheckable(true);
        chip.setOnClickListener(view -> {
            try {
                cg_choose.removeView(view);
                chip.setCloseIconVisible(true);
                cg_pickedLicenses.addView(view);
                pickedLicenses.add(license);
            } catch (Exception e) {

            }
        });
        chip.setOnCloseIconClickListener(view -> {
            cg_pickedLicenses.removeView(view);
            chip.setCloseIconVisible(false);
            cg_choose.addView(view);
            chip.setChecked(true);
            chip.requestFocus();
            pickedLicenses.remove(license);
        });

        cg_choose.addView(chip);
    }
}