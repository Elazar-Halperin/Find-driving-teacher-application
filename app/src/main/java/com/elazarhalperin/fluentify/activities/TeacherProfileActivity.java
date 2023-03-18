package com.elazarhalperin.fluentify.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.helpers.adapters.TeachersFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class TeacherProfileActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;

    ImageView iv_teacherProfile;
    TextView tv_teacherName, tv_workArea;

    TeachersFragmentAdapter fragmentAdapter;
    FragmentManager fragmentManager;
    TeacherModel teacher;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        iv_teacherProfile = findViewById(R.id.iv_teacherProfile);
        tv_teacherName = findViewById(R.id.tv_teacherName);
        tv_workArea = findViewById(R.id.tv_workArea);

        fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new TeachersFragmentAdapter(fragmentManager, getLifecycle());

        image = getIntent().getParcelableExtra("profile_image");
        teacher = (TeacherModel) getIntent().getSerializableExtra("teacher");

        if(image != null) {
            iv_teacherProfile.setImageBitmap(image);
        }


        viewPager.setAdapter(fragmentAdapter);

        fillAllTheFields();

        setListeners();
    }

    private void fillAllTheFields() {
        if(teacher == null) return;

        tv_workArea.setText(teacher.getLocation());
        tv_teacherName.setText((teacher.getName()));
    }

    private void setListeners() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // after the tab is selected we need to change the viewPager fragment.
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }


}