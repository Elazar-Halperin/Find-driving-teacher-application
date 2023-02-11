package com.elazarhalperin.fluentify.helpers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.R;

import java.util.List;

public class TeacherHorizontalAdapter extends RecyclerView.Adapter<TeacherHorizontalAdapter.HorizontalViewHolder> {
    Context context;
    List<TeacherModel> teacherModelList;

    public TeacherHorizontalAdapter(Context context, List<TeacherModel> teacherModelList) {
        this.context = context;
        this.teacherModelList = teacherModelList;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_holder_layout, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return teacherModelList.size();
    }

    public static class HorizontalViewHolder extends RecyclerView.ViewHolder {

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
