package com.elazarhalperin.fluentify.helpers.adapters;

import android.content.Context;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.elazarhalperin.fluentify.R;

import java.util.HashMap;
import java.util.List;

public class ReviewsRVAdapter extends RecyclerView.Adapter<ReviewsRVAdapter.ReviewHolder> {
    List<HashMap<String, Object>> reviews;
    Context context;

    public ReviewsRVAdapter(List<HashMap<String, Object>> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_layout, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        // Get views.
        final ImageView iv_reviewerProfile = holder.getIv_reviewerProfile();
        final RatingBar rb_teacherRate = holder.getRb_teacherRate();
        final TextView tv_reviewerName = holder.getTv_reviewerName();
        final TextView tv_review = holder.getTv_review();
        final TextView tv_reviewDate = holder.getTv_reviewDate();

        // Get the review object.
        HashMap<String, Object> review = reviews.get(position);

        // set text into the review layout..
        rb_teacherRate.setRating(((Double) review.get("rating")).floatValue());
        tv_review.setText(review.get("review").toString());
        tv_reviewDate.setText(review.get("reviewDate").toString());
        tv_reviewerName.setText(review.get("reviewerName").toString());;

        int textColor;
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            textColor = holder.itemView.getContext().getColor(R.color.white);
        } else {
            textColor = holder.itemView.getContext().getColor(R.color.black_text_color);
        }
        tv_reviewerName.setTextColor(textColor);

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder {
        final ImageView iv_reviewerProfile;
        final TextView tv_reviewerName, tv_review, tv_reviewDate;
        final RatingBar rb_teacherRate;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);

            iv_reviewerProfile = itemView.findViewById(R.id.iv_reviewerProfile);
            tv_review = itemView.findViewById(R.id.tv_review);
            tv_reviewerName = itemView.findViewById(R.id.tv_reviewerName);
            tv_reviewDate = itemView.findViewById(R.id.tv_reviewDate);
            rb_teacherRate = itemView.findViewById(R.id.rb_teacherRate);
        }

        public RatingBar getRb_teacherRate() {
            return rb_teacherRate;
        }

        public ImageView getIv_reviewerProfile() {
            return iv_reviewerProfile;
        }

        public TextView getTv_reviewerName() {
            return tv_reviewerName;
        }

        public TextView getTv_review() {
            return tv_review;
        }

        public TextView getTv_reviewDate() {
            return tv_reviewDate;
        }
    }
}
