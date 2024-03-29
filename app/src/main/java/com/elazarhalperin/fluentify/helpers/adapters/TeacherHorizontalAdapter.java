package com.elazarhalperin.fluentify.helpers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.TeacherProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
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
        // put all the text in the TextViews.
        holder.getTv_teacherName().setText(teacherModelList.get(position).getName());
        holder.getTv_locations().setText(teacherModelList.get(position).getLocation());

        // get the reference of the storage where the profile image is stored.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("profile_pictures");
        StorageReference profileImageRef = storageRef.child("profile_image" + teacherModelList.get(position).getUid() + ".jpg");

        final Bitmap[] bmp = new Bitmap[1];
        final long ONE_MEGABYTE = 1024 * 1024;

        // download the image from the firebase storage and put it in the bitmap.
        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytesPrm -> {
            bmp[0] = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.length);
            bmp[0] = Bitmap.createScaledBitmap(bmp[0], bmp[0].getWidth() / 5, bmp[0].getHeight() / 5, true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
            holder.getIv_teacherProfile().setImageBitmap(bmp[0]);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                bmp[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_draw);
                holder.getIv_teacherProfile().setImageBitmap(bmp[0]);
            }
        });


        holder.getCv_container().setOnClickListener(v -> {
            Intent intent = new Intent(context, TeacherProfileActivity.class);

            try {
                intent.putExtra("profile_image", bmp[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.putExtra("teacher", teacherModelList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return teacherModelList.size();
    }

    public static class HorizontalViewHolder extends RecyclerView.ViewHolder {
        final ImageView iv_teacherProfile;
        final TextView tv_teacherName, tv_locations;
        CardView cv_container;
        LinearLayout ll_container;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_teacherProfile = itemView.findViewById(R.id.iv_teacherProfile);
            tv_locations = itemView.findViewById(R.id.tv_teacherLocations);
            tv_teacherName = itemView.findViewById(R.id.tv_teacherName);
            cv_container = itemView.findViewById(R.id.cv_container);
            ll_container = itemView.findViewById(R.id.ll_container);
        }

        public LinearLayout getLl_container() {
            return ll_container;
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

        public CardView getCv_container() {
            return cv_container;
        }
    }
}
