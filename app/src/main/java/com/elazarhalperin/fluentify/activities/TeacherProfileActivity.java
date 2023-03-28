package com.elazarhalperin.fluentify.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.fragments.TeacherInfoFragment;
import com.elazarhalperin.fluentify.helpers.adapters.TeachersFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class TeacherProfileActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;

    ImageView iv_teacherProfile;
    TextView tv_teacherName, tv_teachingLocations, tv_licenses, tv_rating, tv_teacherInfo, tv_lessonPrice;

    TeachersFragmentAdapter fragmentAdapter;
    FragmentManager fragmentManager;
    TeacherModel teacher;
    Bitmap image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        iv_teacherProfile = findViewById(R.id.iv_teacherProfile);
        tv_teacherName = findViewById(R.id.tv_teacherName);
        tv_teachingLocations = findViewById(R.id.tv_teacherLocations);
        tv_licenses = findViewById(R.id.tv_licenses);
        tv_rating = findViewById(R.id.tv_ratring);
        tv_teacherInfo = findViewById(R.id.tv_teacherInfo);
        tv_lessonPrice = findViewById(R.id.tv_lessonPrice);



        tv_teachingLocations.setSelected(true);

        fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new TeachersFragmentAdapter(fragmentManager, getLifecycle());

        image = getIntent().getParcelableExtra("profile_image");
        teacher = (TeacherModel) getIntent().getSerializableExtra("teacher");

        if(image != null) {
            iv_teacherProfile.setImageBitmap(image);
        }




        fillAllTheFields();

        setListeners();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String resource_id = "5c78e9fa-c2e2-4771-93ff-7f400a12f7ba";
//                    String url = "https://data.gov.il/api/3/action/datastore_search?resource_id=" + resource_id;
//                    URLConnection connection = new URL(url).openConnection();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    StringBuilder result = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        result.append(line);
//                    }
//                    reader.close();
//                    Log.d("cities", result.toString());
//                } catch (Exception e) {
//                    Log.d("cities", "error");
//                    e.printStackTrace();
//                }
//            }
//        }).start();


    }

    private void fillAllTheFields() {
        if(teacher == null) return;

//        tv_workArea.setText(teacher.getLocation());
        tv_teacherName.setText((teacher.getName()));
    }

    private void setListeners() {

    }


}