package com.elazarhalperin.fluentify.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.fragments.TeacherInfoFragment;
import com.elazarhalperin.fluentify.helpers.TeachersFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class TeacherProfileActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TeachersFragmentAdapter fragmentAdapter;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);


        fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new TeachersFragmentAdapter(fragmentManager, getLifecycle());

        viewPager.setAdapter(fragmentAdapter);

        setListeners();
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