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
import java.util.Locale;

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
    List<String> licensesHe;

    Uri selectedImage;

    String phoneNumber;

    NavController navController;

    ArrayAdapter<String> listCitiesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            // get the phone number
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

        // get the cities and move it into arrayAdapter.
        cities = Arrays.asList(getResources().getStringArray(R.array.cities));

        listCitiesAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_city_item_layout,new ArrayList<>(cities));
        et_teachingPlaces.setAdapter(listCitiesAdapter);



        // get the list of hebrew and english.
        List<String> licenses = Arrays.asList(getResources().getStringArray(languageCode.equals(new Locale("he").getLanguage()) ? R.array.licenses_he : R.array.licenses_en));
        licensesEn = Arrays.asList(getResources().getStringArray(R.array.licenses_en));
        licensesHe = Arrays.asList(getResources().getStringArray(R.array.licenses_he));

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

    /**
     * Navigates to the next fragment after validating the input fields.
     * If any of the fields is empty or invalid, an error message is displayed.
     */
    private void goToNextFragment() {
        // Retrieve input field values
        String name = et_name.getText().toString().trim();
        String info = et_extraInfo.getText().toString().trim();
        String price = et_lessonPrice.getText().toString().trim();
        String locations = et_teachingPlaces.getText().toString().trim();

        // Validate input fields
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

        // Validate profile image
        if (selectedImage == null) {
            Toast.makeText(getActivity(), "Profile image is required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double d = Double.parseDouble(price);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Only numeric values are allowed for the price.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = getBundleFromFields(name, info, price, locations);

        navController.navigate(R.id.action_extraTeacherDataFragment_to_finalSignUpTeacherFragment, bundle);
    }

    /**
     * Creates a bundle containing the input field values.
     *
     * @param name      The name value.
     * @param info      The info value.
     * @param price     The price value.
     * @param locations The locations value.
     * @return The bundle containing the field values.
     */
    private Bundle getBundleFromFields(String name, String info, String price, String locations) {
        Bundle bundle = new Bundle();
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

    /**
     * Generates a chip with the provided license and adds it to the layout.
     *
     * @param license The license text.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void generateChip(String license) {
        Chip chip = new Chip(getActivity());
        // set the chip text.
        chip.setText(license);
        // set the icon
        chip.setChipIcon(
                getResources().getDrawable(R.drawable.ic_launcher_foreground, getActivity().getTheme())
        );
        chip.setChipIconVisible(false);
        chip.setClickable(true);
        // set on click listener to the chip
        chip.setOnClickListener(view -> {
            try {
                // when chip is choosed
                // remove it from this chipGroup and transfer to other
                cg_choose.removeView(view);
                chip.setCloseIconVisible(true);
                cg_pickedLicenses.addView(view);

                // change the language of license if the app is in heb
                int position = licensesHe.indexOf(license);
                if (position >= 0) {
                    pickedLicenses_en.add(licensesEn.get(position));
                } else {
                    pickedLicenses_en.add(license);
                }
            } catch (Exception e) {
                // Handle any exceptions
            }
        });
        chip.setOnCloseIconClickListener(view -> {
            // when chip is choosed
            // remove it from this chipGroup and transfer to other
            cg_pickedLicenses.removeView(view);
            chip.setCloseIconVisible(false);
            // transfer to other
            cg_choose.addView(view);
            chip.setChecked(true);
            chip.requestFocus();

            // change the language of license if the app is in heb

            int position = licensesHe.indexOf(license);
            if (position >= 0) {
                pickedLicenses_en.remove(licensesEn.get(position));
            } else {
                pickedLicenses_en.remove(license);
            }
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