package com.elazarhalperin.fluentify.helpers.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elazarhalperin.fluentify.Models.CategoryModel;
import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.MyHolder> {
    List<CategoryModel> sectionList;
    Context context;
    FirebaseFirestore db;

    public SectionRecyclerViewAdapter(Context context, List<CategoryModel> sectionList) {
        this.sectionList = sectionList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public SectionRecyclerViewAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_layout, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final TextView tv_filtered = holder.getTv_filtered();
        final Button btn_viewAll = holder.getBtn_viewAll();
        final RecyclerView rv_teachers = holder.getRv_teachers();

        tv_filtered.setText(sectionList.get(position).getName());

        rv_teachers.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        List<TeacherModel> teachersList = sectionList.get(position).getTeachers();

        TeacherHorizontalAdapter adapter = new TeacherHorizontalAdapter(context, teachersList);
        rv_teachers.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        final TextView tv_filtered;
        final Button btn_viewAll;
        final RecyclerView rv_teachers;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tv_filtered = itemView.findViewById(R.id.tv_filtered);
            btn_viewAll = itemView.findViewById(R.id.btn_viewAll);
            rv_teachers = itemView.findViewById(R.id.rv_teachers);
        }

        public Button getBtn_viewAll() {
            return btn_viewAll;
        }

        public RecyclerView getRv_teachers() {
            return rv_teachers;
        }

        public TextView getTv_filtered() {
            return tv_filtered;
        }
    }
}
