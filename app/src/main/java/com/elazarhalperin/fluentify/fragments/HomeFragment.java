package com.elazarhalperin.fluentify.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.ChatListActivity;
import com.elazarhalperin.fluentify.activities.TeacherProfileActivity;
import com.elazarhalperin.fluentify.helpers.adapters.SectionRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView rv_sections;
    FloatingActionButton fab_goToChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView ibn = view.findViewById(R.id.iv_profilePicture);
        rv_sections = view.findViewById(R.id.rv_units);
        fab_goToChats = view.findViewById(R.id.fab_goToChats);

        List<String> strings = new ArrayList<>();
        strings.add("elazar the king");
        strings.add("kaki");
        strings.add("pip");

        SectionRecyclerViewAdapter adapter = new SectionRecyclerViewAdapter(getActivity(), strings);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_sections.setLayoutManager(layoutManager);
        rv_sections.setAdapter(adapter);

        ibn.setOnClickListener(v-> {
            Intent i = new Intent(getActivity(), TeacherProfileActivity.class);
            startActivity(i);
        });

        fab_goToChats.setOnClickListener(v-> {
            Intent i = new Intent(getActivity(), ChatListActivity.class);
            startActivity(i);
        });
    }
}