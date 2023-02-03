package com.elazarhalperin.fluentify.helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.elazarhalperin.fluentify.fragments.TeacherInfoFragment;
import com.elazarhalperin.fluentify.fragments.TeacherReviewsFragment;

public class TeachersFragmentAdapter extends FragmentStateAdapter {
    public TeachersFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 1)
            return new TeacherReviewsFragment();

        return new TeacherInfoFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
