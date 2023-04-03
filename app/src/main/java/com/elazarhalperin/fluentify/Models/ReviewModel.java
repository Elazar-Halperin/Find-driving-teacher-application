package com.elazarhalperin.fluentify.Models;

import java.io.Serializable;
import java.util.HashMap;

public class ReviewModel implements Serializable {
    String reviewerUid;
    double rating;
    String review;
    String reviewDate;
    String reviewerName;

    public ReviewModel(String reviewerUid, double rating, String review, String reviewDate, String reviewerName) {
        this.reviewerUid = reviewerUid;
        this.rating = rating;
        this.review = review;
        this.reviewDate = reviewDate;
        this.reviewerName = reviewerName;
    }

    public ReviewModel() {
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReviewerUid() {
        return reviewerUid;
    }


    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
}
