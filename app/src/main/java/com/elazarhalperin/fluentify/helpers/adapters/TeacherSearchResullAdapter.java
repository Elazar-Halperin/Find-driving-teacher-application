package com.elazarhalperin.fluentify.helpers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.R;
import com.elazarhalperin.fluentify.activities.TeacherProfileActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TeacherSearchResullAdapter extends RecyclerView.Adapter<TeacherSearchResullAdapter.HorizontalViewHolder> {
    Context context;
    List<TeacherModel> teacherModelList;


    public TeacherSearchResullAdapter(Context context, List<TeacherModel> teacherModelList) {
        this.context = context;
        this.teacherModelList = teacherModelList;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_search_result_layout, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        holder.getTv_teacherName().setText(teacherModelList.get(position).getName());
        holder.getTv_locations().setText(teacherModelList.get(position).getLocation());
        holder.getTv_lessonPrice().setText(teacherModelList.get(position).getLessonPrice() + context.getString(R.string.per_lesson));
        holder.getTv_rating().setText(String.valueOf(teacherModelList.get(position).getRating()));

        holder.getShimmerFrameLayout().startShimmer();

        TeacherModel teacherModel = teacherModelList.get(position);

        String languageCode = context.getResources().getConfiguration().locale.getLanguage(); // get the current language code

        if(languageCode.equals(new Locale("he").getLanguage())) {
            List<String> teacherModelLicenses = teacherModel.getLicenses();
            List<String> licenses_en = Arrays.asList(context.getResources().getStringArray(R.array.licenses_en));
            List<String> licenses_he = Arrays.asList(context.getResources().getStringArray(R.array.licenses_he));

            List<String> result = new ArrayList<>();
            for(String license : teacherModelLicenses) {
                int index = licenses_en.indexOf(license);
                if(index < 0) continue;

                result.add(licenses_he.get(index));
            }
            Log.d("loch", "you are in hebrew language\nthis is your result " + result   );

            teacherModel.setLicenses(result);
            holder.getTv_licenses().setText(String.join(",", result));

        } else {
            holder.getTv_licenses().setText(String.join(",",teacherModelList.get(position).getLicenses().toString()));

        }


        // Get the image storage location.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("profile_pictures");
        StorageReference profileImageRef = storageRef.child("profile_image" + teacherModelList.get(position).getUid()+".jpg");

        final Bitmap[] bmp = new Bitmap[1];
        final long ONE_MEGABYTE = 1024 * 1024;
        // downloading the images from the storage.
        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytesPrm -> {
            bmp[0] = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.length);
            bmp[0] = Bitmap.createScaledBitmap(bmp[0], bmp[0].getWidth() /4, bmp[0].getHeight() / 4, true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp[0].compress(Bitmap.CompressFormat.PNG, 100, stream);

            holder.getShimmerFrameLayout().stopShimmer();
            holder.getShimmerFrameLayout().setVisibility(View.GONE);
            holder.getIv_teacherProfile().setVisibility(View.VISIBLE);
            holder.getIv_teacherProfile().setImageBitmap(bmp[0]);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                 bmp[0] =  BitmapFactory.decodeResource(context.getResources(), R.drawable.person_draw);
                holder.getShimmerFrameLayout().stopShimmer();
                holder.getShimmerFrameLayout().setVisibility(View.GONE);
                holder.getIv_teacherProfile().setVisibility(View.VISIBLE);

                holder.getIv_teacherProfile().setImageResource(R.drawable.person_draw);
            }
        });

        holder.getCv_container().setOnClickListener( v-> {
            Intent intent = new Intent(context, TeacherProfileActivity.class);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context);
            intent.putExtra("profile_image", bmp[0]);
            intent.putExtra("teacher", teacherModel);

            context.startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return teacherModelList.size();
    }

    public static class HorizontalViewHolder extends RecyclerView.ViewHolder {
        final ImageView iv_teacherProfile;
        final TextView tv_teacherName, tv_locations,tv_licenses, tv_rating, tv_lessonPrice;
        final ShimmerFrameLayout shimmerFrameLayout;
        CardView cv_container;
        LinearLayout ll_container;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_teacherProfile = itemView.findViewById(R.id.iv_teacherProfile);

            tv_locations = itemView.findViewById(R.id.tv_teacherLocations);
            tv_teacherName = itemView.findViewById(R.id.tv_teacherName);
            tv_rating = itemView.findViewById(R.id.tv_ratring);
            tv_lessonPrice = itemView.findViewById(R.id.tv_lessonPrice);
            tv_licenses = itemView.findViewById(R.id.tv_licenses);

            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_imageHolder);

            cv_container = itemView.findViewById(R.id.cv_container);
            ll_container = itemView.findViewById(R.id.ll_container);
        }

        public ShimmerFrameLayout getShimmerFrameLayout() {
            return shimmerFrameLayout;
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

        public TextView getTv_lessonPrice() {
            return tv_lessonPrice;
        }

        public TextView getTv_licenses() {
            return tv_licenses;
        }

        public TextView getTv_rating() {
            return tv_rating;
        }

        public CardView getCv_container() {
            return cv_container;
        }
    }
}
