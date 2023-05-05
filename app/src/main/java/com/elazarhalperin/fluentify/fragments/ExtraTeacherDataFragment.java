package com.elazarhalperin.fluentify.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.elazarhalperin.fluentify.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtraTeacherDataFragment extends Fragment {
    private static final int GO_TO_GALLERY_CODE = 121;
    ImageView iv_profileImage;
    EditText et_name, et_extraInfo, et_lessonPrice;
    AutoCompleteTextView et_teachingPlaces;
    ChipGroup cg_choose, cg_pickedLicenses;
    Button btn_next;

    List<String> pickedLicenses_en;
    List<String> pickedLicenses_he;
    List<String> cities;
    List<String> licensesEn;

    Uri selectedImage;

    String phoneNumber;

    NavController navController;

    ArrayAdapter<String> listCitiesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            phoneNumber = bundle.getString("phoneNumber");
        } else {
            Toast.makeText(getActivity(), "wtf is going on", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_extra_teacher_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String languageCode = getResources().getConfiguration().locale.getLanguage();

        cg_choose = view.findViewById(R.id.cg_choose);
        cg_pickedLicenses = view.findViewById(R.id.cg_pickedLicense);
        iv_profileImage = view.findViewById(R.id.iv_profileImage);
        et_name = view.findViewById(R.id.et_teacherName);
        et_extraInfo = view.findViewById(R.id.et_extraInfo);
        et_teachingPlaces = view.findViewById(R.id.et_whereYouTeach);
        et_lessonPrice = view.findViewById(R.id.et_lessonPrice);
        btn_next = view.findViewById(R.id.btn_next);

        pickedLicenses_en = new ArrayList<>();
        pickedLicenses_he = new ArrayList<>();

        navController = Navigation.findNavController(view);

        cities = Arrays.asList(getResources().getStringArray(R.array.cities));

        listCitiesAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_city_item_layout,new ArrayList<>(cities));
        et_teachingPlaces.setAdapter(listCitiesAdapter);



        List<String> licenses = Arrays.asList(getResources().getStringArray(languageCode.equals("he") ? R.array.licenses_he : R.array.licenses_en));
        licensesEn = Arrays.asList(getResources().getStringArray(languageCode.equals("he") ? R.array.licenses_he : R.array.licenses_en));

        for (String license : licenses)
            generateChip(license);

        setListeners();
    }

    private void setListeners() {
        iv_profileImage.setOnClickListener(v -> {
            Intent intentImage = new Intent();
            intentImage.setType("image/*");
            intentImage.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intentImage, GO_TO_GALLERY_CODE);
        });

        btn_next.setOnClickListener(v -> {
            goToNextFragment();
        });

    }

    private void goToNextFragment() {
        String name = et_name.getText().toString().trim();
        String info = et_extraInfo.getText().toString().trim();
        String price = et_lessonPrice.getText().toString().trim(); // later we will turn it into double.
        String locations = et_teachingPlaces.getText().toString().trim();

        // checking if any of the fields is empty.
        if (name == null || name.isEmpty()) {
            et_name.requestFocus();
            et_name.setError("Please fill the field!");
            return;
        }
        if (info == null || info.isEmpty()) {
            et_extraInfo.requestFocus();
            et_extraInfo.setError("Please fill the field!");
            return;
        }
        if (price == null || price.isEmpty()) {
            et_lessonPrice.requestFocus();
            et_lessonPrice.setError("Please fill the field!");
            return;
        }
        if (locations == null || locations.isEmpty()) {
            et_teachingPlaces.requestFocus();
            et_teachingPlaces.setError("Please fill the field!");
            return;
        }

        if(selectedImage == null) {
            Toast.makeText(getActivity(), "Profile image is required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double d = Double.parseDouble(price);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "only numeric is possible.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = getBundleFromFields(name, info, price, locations);

        navController.navigate(R.id.action_extraTeacherDataFragment_to_finalSignUpTeacherFragment, bundle);

    }

    private Bundle getBundleFromFields(String name, String info, String price, String locations) {

        Bundle bundle = new Bundle();
        // turning the price into double.
        double lessonPrice = Double.parseDouble(price);

        bundle.putString("name", name);
        bundle.putString("info", info);
        bundle.putDouble("lessonPrice", lessonPrice);
        bundle.putString("locations", locations);
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putStringArrayList("licenses", (ArrayList<String>) pickedLicenses_en);
        bundle.putParcelable("selectedImage", selectedImage);

        return bundle;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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
                pickedLicenses_en.add(license);
            } catch (Exception e) {

            }
        });
        chip.setOnCloseIconClickListener(view -> {
            cg_pickedLicenses.removeView(view);
            chip.setCloseIconVisible(false);
            cg_choose.addView(view);
            chip.setChecked(true);
            chip.requestFocus();
            pickedLicenses_en.remove(license);
        });

        cg_choose.addView(chip);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GO_TO_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            iv_profileImage.setImageURI(selectedImage);
        }
    }
}