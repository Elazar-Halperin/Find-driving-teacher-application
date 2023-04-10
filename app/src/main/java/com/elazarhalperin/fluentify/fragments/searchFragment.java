package com.elazarhalperin.fluentify.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.TeacherProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link searchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class searchFragment extends Fragment {
    FloatingActionButton fab_filter, fab_clearFilter;
    RecyclerView rv_teachers;
    ChipGroup cg_holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        fab_clearFilter = v.findViewById(R.id.fab_search);
        fab_filter = v.findViewById(R.id.fab_filter);
        rv_teachers = v.findViewById(R.id.rv_teachers);


        setListeners();

    }

    private void setListeners() {
        fab_filter.setOnClickListener(v-> {
            showDialog();
        });
        fab_clearFilter.setOnClickListener(v-> {

        });


    }

    void showDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.DialogStyle);
        dialog.setContentView(R.layout.filter_layout);
        // get all the neccessery views
        cg_holder = dialog.findViewById(R.id.cg_holder);

        for(int i = 0; i < cg_holder.getChildCount(); i++) {
            Chip chip = (Chip) cg_holder.getChildAt(i);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check whether the chips is filtered by user
                    // or not and give the suitable Toast message
                    if (chip.isChecked()) {
                        chip.setBackgroundColor(getActivity().getColor(R.color.orange_color));
                    } else {
                        chip.setBackgroundColor(getActivity().getColor(R.color.shimmer_color));
                    }
                }
            });
        }

        dialog.show();
    }
}