package com.elazarhalperin.fluentify.helpers.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        holder.getTv_teacherName().setText(teacherModelList.get(position).getName());
        holder.getTv_locations().setText(teacherModelList.get(position).getLocation());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("profile_pictures");
        StorageReference profileImageRef = storageRef.child("profile_image" + teacherModelList.get(position).getUid()+".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytesPrm -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.length);
            holder.getIv_teacherProfile().setImageBitmap(bmp);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.getIv_teacherProfile().setImageResource(R.drawable.person_draw);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teacherModelList.size();
    }

    public static class HorizontalViewHolder extends RecyclerView.ViewHolder {
        final ImageView iv_teacherProfile;
        final TextView tv_teacherName, tv_locations;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_teacherProfile = itemView.findViewById(R.id.iv_teacherProfile);
            tv_locations = itemView.findViewById(R.id.tv_teacherLocations);
            tv_teacherName = itemView.findViewById(R.id.tv_teacherName);
        }

        public ImageView getIv_teacherProfile() {
            return iv_teacherProfile;
        }

        public TextView getTv_teacherName() {
            return tv_teacherName;
        }

        public TextView getTv_locations() {
            return tv_locations;
        }
    }
}
