package com.elazarhalperin.fluentify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.UserTypeHelper;
import com.elazarhalperin.fluentify.helpers.adapters.ReviewsRVAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TeacherProfileActivity extends AppCompatActivity {

    ImageView iv_teacherProfile;
    TextView tv_teacherName, tv_teachingLocations, tv_licenses, tv_rating, tv_teacherInfo, tv_lessonPrice;

    RecyclerView rv_reviews;
    ReviewsRVAdapter adapter;
    List<HashMap<String, Object>> reviews;

    FloatingActionButton fab_close, fab_addReview, fab_sendAMessage;

    MotionLayout motionLayout;

    TeacherModel teacher;
    Bitmap image;

    FirebaseUser firebaseUser;

    UserTypeHelper userTypeHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        // for the animation.
        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        userTypeHelper = new UserTypeHelper(getApplicationContext());


        iv_teacherProfile = findViewById(R.id.iv_teacherProfile);

        tv_teacherName = findViewById(R.id.tv_teacherName);
        tv_teachingLocations = findViewById(R.id.tv_teacherLocations);
        tv_licenses = findViewById(R.id.tv_licenses2);
        tv_rating = findViewById(R.id.tv_ratring);
        tv_teacherInfo = findViewById(R.id.tv_teacherInfo);
        tv_lessonPrice = findViewById(R.id.tv_lessonPrice);

        fab_close = findViewById(R.id.fab_close);
        fab_addReview = findViewById(R.id.fab_addReview);
        fab_sendAMessage = findViewById(R.id.fab_sendAMessage);

        rv_reviews = findViewById(R.id.rv_reviews);

        motionLayout = findViewById(R.id.motionLayout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        tv_teachingLocations.setSelected(true);

        // Get the image and teacher from the intent extras
        image = getIntent().getParcelableExtra("profile_image");
        teacher = (TeacherModel) getIntent().getSerializableExtra("teacher");
        reviews = teacher.getReviews();

        adapter = new ReviewsRVAdapter(reviews, getApplicationContext());

        rv_reviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_reviews.setAdapter(adapter);

        // check if image is null
        if (image == null || image.equals(BitmapFactory.decodeResource(getResources(), R.drawable.person_draw))) {
            setImageFromStorage();
        } else {
            iv_teacherProfile.setImageBitmap(image);
        }

        fillAllTheFields();

        setListeners();
    }

    private void setImageFromStorage() {

    }

    private void fillAllTheFields() {
        if (teacher == null) return;

        tv_teacherName.setText((teacher.getName()));
        tv_lessonPrice.setText(teacher.getLessonPrice() + getString(R.string.per_lesson));
        tv_teacherInfo.setText(teacher.getInfo());
        tv_rating.setText(teacher.getRating() + "");
        tv_teachingLocations.setText(teacher.getLocation());

        List<String> licenses_en = Arrays.asList(getResources().getStringArray( R.array.licenses_en));
        List<String> licenses_he = Arrays.asList(getResources().getStringArray(R.array.licenses_he));


        tv_licenses.setText(String.join(",", teacher.getLicenses()));
    }

    private void setListeners() {
        fab_close.setOnClickListener(v -> {
            finish();
        });

        fab_sendAMessage.setOnClickListener(v -> {
            if (isTeacher()) {
                Toast.makeText(getApplicationContext(), "You can't send a message to a teacher!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("Teacher", teacher);
            intent.putExtra("messageTo", teacher.getUid());
            startActivity(intent);
        });

        fab_addReview.setOnClickListener(v -> {
            if (isTeacher()) {
                Toast.makeText(getApplicationContext(), "You can't send a message to a teacher!", Toast.LENGTH_SHORT).show();
                return;
            }
            showDialog();
        });

        // After the motionLayout animation is in end state setting the fAB to disabled.
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                boolean isEnabled;
                if (currentId == motionLayout.getEndState()) {
                    isEnabled = false;
                } else {
                    isEnabled = true;
                }
                fab_sendAMessage.setEnabled(isEnabled);
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });
    }

    private boolean isTeacher() {
        if (userTypeHelper.getUserType().equals(UserTypeHelper.TEACHER_TYPE)) return true;
        return false;
    }

    void showDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(TeacherProfileActivity.this, R.style.DialogStyle);
        dialog.setContentView(R.layout.create_review_layout);
        // get all the neccessery views
        RatingBar rb_rate = dialog.findViewById(R.id.rb_rate);
        EditText et_review = dialog.findViewById(R.id.et_review);
        FloatingActionButton fab_sendReview = dialog.findViewById(R.id.fab_sentReview);


        if (isReviewExist()) return;

        fab_sendReview.setOnClickListener(v2 -> {
            if (et_review.getText().toString() == null || et_review.getText().toString().isEmpty()) {
                return;
            }
            // getting all the views to necessary fields.
            double rating = (double) rb_rate.getRating();
            String textReview = et_review.getText().toString().trim();

            // Create a LocalDate object with the current date
            LocalDate currentDate = LocalDate.now();
            // Create a DateTimeFormatter object to format the date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            // Format the date as a string using the formatter
            String dateString = currentDate.format(formatter);


            // making the review model to a map so it can be added into the firebase.
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("rating", rating);
            hashMap.put("review", textReview);
            hashMap.put("reviewDate", dateString);
            hashMap.put("reviewerName", "Anonymous");
            hashMap.put("reviewerUid", firebaseUser.getUid());
            // adding the review into the reviews list
            reviews.add(hashMap);

            DocumentReference db = FirebaseFirestore.getInstance().collection("teachers").document(teacher.getUid());

            db.update("reviews", reviews).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Your review was added successfully!", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();

                        double teacherFinalRating = (rating + teacher.getRating()) / reviews.size();

                        db.update("rating", teacherFinalRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "failed to add your review!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });

        // Adding a listener when the text is changed, so the send review button can be revealed after there are text.
        et_review.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.toString().isEmpty()) {
                    fab_sendReview.setVisibility(View.GONE);
                } else {
                    fab_sendReview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        dialog.show();
    }

    private boolean isReviewExist() {
        for (HashMap map : reviews) {
            if (map.containsValue(firebaseUser.getUid())) {
                Toast.makeText(getApplicationContext(), "You have already made an a review...", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }


}