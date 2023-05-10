package com.elazarhalperin.fluentify.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.elazarhalperin.fluentify.Models.CategoryModel;
import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.ChatListActivity;
import com.elazarhalperin.fluentify.activities.TeacherProfileActivity;
import com.elazarhalperin.fluentify.helpers.adapters.SectionRecyclerViewAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.chrono.JapaneseDate;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView rv_sections;
    FloatingActionButton fab_goToChats;
    ShimmerFrameLayout shimmer;

    CollectionReference teacherRef;
    SectionRecyclerViewAdapter adapter;
    List<CategoryModel> categories;
    private static final String ARG_CATEGORIES = "categories";

    public static HomeFragment newInstance(ArrayList<CategoryModel> categories) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORIES, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Toast.makeText(getActivity(), "feels good ", Toast.LENGTH_SHORT).show();
            categories = (List<CategoryModel>) getArguments().getSerializable(ARG_CATEGORIES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_sections = view.findViewById(R.id.rv_units);
        fab_goToChats = view.findViewById(R.id.fab_goToChats);
        shimmer = view.findViewById(R.id.shimmer_sectionHolder);

        teacherRef = FirebaseFirestore.getInstance().collection("teachers");


        if (categories == null || categories.isEmpty()) {
            categories = new ArrayList<>();
            Toast.makeText(getActivity(), "feels bad", Toast.LENGTH_SHORT).show();
            // to start the skeletion effect
            shimmer.startShimmer();



        } else {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
            rv_sections.setVisibility(View.VISIBLE);
        }
        adapter = new SectionRecyclerViewAdapter(getActivity(), categories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_sections.setLayoutManager(layoutManager);
        rv_sections.setAdapter(adapter);

        fab_goToChats.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ChatListActivity.class);
            startActivity(i);
        });
        if (categories == null || categories.isEmpty()) {
            getAllTeachersByCategories();
        }
    }

    private void getAllTeachersByCategories() {
        // getting al the queries by categories and prefernces ( this is just a demo for me)
        Query price = teacherRef.whereLessThanOrEqualTo("lessonPrice", 140.00).limit(10);
        Query rating = teacherRef.whereGreaterThanOrEqualTo("rating", 1.0).limit(10);
        Query jerusalemTeachers = teacherRef.whereGreaterThanOrEqualTo("location", "jerusalem").limit(10);
        Query haderaTeachers = teacherRef.whereGreaterThanOrEqualTo("location", "hadera").limit(10);

        CategoryModel categoryPrice = new CategoryModel("Average Price", new ArrayList<>());
        CategoryModel categoryrating = new CategoryModel("Good Rating", new ArrayList<>());
        CategoryModel categoryJerusalem = new CategoryModel("Jerusalem Area", new ArrayList<>());
        CategoryModel categoryHadera = new CategoryModel("Hadera Area", new ArrayList<>());

        // Execute the queries in parallel using Tasks.whenAllSuccess
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        tasks.add(price.get());
        tasks.add(rating.get());
        tasks.add(jerusalemTeachers.get());
        tasks.add(haderaTeachers.get());

        Tasks.whenAllSuccess(tasks).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
            @Override
            public void onComplete(@NonNull Task<List<Object>> task) {
                if (task.isSuccessful()) {
                    List<QuerySnapshot> querySnapshots = new ArrayList<>();
                    for (Object result : task.getResult()) {
                        if (result instanceof QuerySnapshot) {
                            querySnapshots.add((QuerySnapshot) result);
                        }
                    }


                    // Extract the results from each query snapshot
                    List<TeacherModel> priceTeachers = extractTeachers(querySnapshots.get(0));
                    List<TeacherModel> ratingTeachers = extractTeachers(querySnapshots.get(1));
                    List<TeacherModel> jerusalemTeachers = extractTeachers(querySnapshots.get(2));
                    List<TeacherModel> haderaTeachers = extractTeachers(querySnapshots.get(3));

                    categoryPrice.setTeachers(priceTeachers);
                    categoryrating.setTeachers(ratingTeachers);
                    categoryHadera.setTeachers(haderaTeachers);
                    categoryJerusalem.setTeachers(jerusalemTeachers);



                    categories.add(categoryPrice);
                    categories.add(categoryrating);
                    categories.add(categoryJerusalem);
                    categories.add(categoryHadera);


                    adapter.notifyDataSetChanged();

                    shimmer.stopShimmer();
                    shimmer.setVisibility(View.GONE);
                    rv_sections.setVisibility(View.VISIBLE);

                    // Pass the results to your RecyclerView adapters for display
                    // e.g. myPriceAdapter.setTeachers(priceTeachers);
                    //      myRatingAdapter.setTeachers(ratingTeachers);
                    //      myNewTeachersAdapter.setTeachers(newTeachers);
                    //      myJerusalemTeachersAdapter.setTeachers(jerusalemTeachers);
                    //      myHaderaTeachersAdapter.setTeachers(haderaTeachers);
                } else {
                    // Handle query error here
                    Exception exception = task.getException();
                    // ...
                }
            }
        });


    }

    private List<TeacherModel> extractTeachers(QuerySnapshot querySnapshot) {
        List<TeacherModel> teachers = new ArrayList<>();
        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
            // Assuming your Teacher class has a constructor that takes a DocumentSnapshot
            TeacherModel teacher = new TeacherModel(documentSnapshot.getData());
            teachers.add(teacher);
        }
        return teachers;
    }


}