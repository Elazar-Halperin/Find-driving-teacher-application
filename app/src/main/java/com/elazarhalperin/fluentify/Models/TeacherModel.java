package com.elazarhalperin.fluentify.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherModel extends UserModel {
    double lessonPrice;
    double rating;
    String location;
    String info;
    String phoneNumber;
    List<String> licences;
    List<ReviewModel> reviews;

    public TeacherModel() {
    }


    public TeacherModel(String uid, String name, String email, String signUpDate, double lessonPrice,  String location, String info, String phoneNumber, List<String> licences) {
        super(uid, name, email, signUpDate);
        this.lessonPrice = lessonPrice;
        this.rating = 0.0d;
        this.location = location;
        this.info = info;
        this.phoneNumber = phoneNumber;
        this.licences = licences;
        reviews = new ArrayList<>();
    }

    public TeacherModel(Map<String, Object> mapTeacher) {
        super.uid = (String) mapTeacher.get("uid");
        super.name = (String) mapTeacher.get("name");
        super.email = (String) mapTeacher.get("email");
        super.signUpDate = (String) mapTeacher.get("signUpDate");
        this.lessonPrice = (double) mapTeacher.get("lessonPrice");
        this.rating = (double) mapTeacher.get("rating");
        this.location = (String) mapTeacher.get("location");
        this.info = (String) mapTeacher.get("info");
        this.phoneNumber = (String) mapTeacher.get("phoneNumber");
        this.licences = (List<String>) mapTeacher.get("licences");
        this.reviews = (List<ReviewModel>) mapTeacher.get("reviews");
    }

    public Map getMap()
    {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("name",name);
        user.put("email",email);
        user.put("signUpDate",signUpDate);
        user.put("lessonPrice",lessonPrice);
        user.put("rating",rating);
        user.put("location",location);
        user.put("info",info);
        user.put("phoneNumber",phoneNumber);
        user.put("licences",licences);
        user.put("reviews", reviews);
        return user;
    }

    @Override
    public String toString() {
        return "TeacherModel{" +
                "lessonPrice=" + lessonPrice +
                ", rating=" + rating +
                ", location='" + location + '\'' +
                ", info='" + info + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", licences=" + licences +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", signUpDate='" + signUpDate + '\'' +
                '}';
    }

    public double getLessonPrice() {
        return lessonPrice;
    }

    public void setLessonPrice(double lessonPrice) {
        this.lessonPrice = lessonPrice;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getLicences() {
        return licences;
    }

    public void setLicences(List<String> licences) {
        this.licences = licences;
    }

    public List<ReviewModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewModel> reviews) {
        this.reviews = reviews;
    }
}
