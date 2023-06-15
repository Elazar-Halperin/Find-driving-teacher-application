package com.elazarhalperin.fluentify.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherModel extends UserModel implements Serializable {
    double lessonPrice; // price per lesson
    double rating; // rating of the teacher
    String location; // location where he teaches.
    String info; // info about him
    String phoneNumber; // his phone number
    List<String> licenses; // the licenses that he teaches.
    List<HashMap<String, Object>> reviews; // his reviews about htm

    public TeacherModel() {
    }


    public TeacherModel(String uid, String name, String email, String signUpDate, String signUpDate_he, double lessonPrice,  String location, String info, String phoneNumber, List<String> licences) {
        super(uid, name, email, signUpDate, signUpDate_he);
        this.lessonPrice = lessonPrice;
        this.rating = 0.0d;
        this.location = location;
        this.info = info;
        this.phoneNumber = phoneNumber;
        this.licenses = licences;
        reviews = new ArrayList<>();
    }

    // have a constructor of map object
    // because firebase retrieves map objects
    // we turn the map object into teacher object.
    public TeacherModel(Map<String, Object> mapTeacher) {
        super.uid = (String) mapTeacher.get("uid");
        super.name = (String) mapTeacher.get("name");
        super.email = (String) mapTeacher.get("email");
        super.signUpDate = (String) mapTeacher.get("signUpDate");
        super.signUpDate_he = (String) mapTeacher.get("signUpDate_he");
        this.lessonPrice = (double) mapTeacher.get("lessonPrice");
        this.rating = (double) mapTeacher.get("rating");
        this.location = (String) mapTeacher.get("location");
        this.info = (String) mapTeacher.get("info");
        this.phoneNumber = (String) mapTeacher.get("phoneNumber");
        this.licenses = (List<String>) mapTeacher.get("licences");
        this.reviews = (List<HashMap<String, Object>>) mapTeacher.get("reviews");
    }

    /*
    get the user as map so we can modify it better in the firebase.
     */
    public Map getMap()
    {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("name",name);
        user.put("email",email);
        user.put("signUpDate",signUpDate);
        user.put("signUpDate_he",signUpDate);
        user.put("lessonPrice",lessonPrice);
        user.put("rating",rating);
        user.put("location",location);
        user.put("info",info);
        user.put("phoneNumber",phoneNumber);
        user.put("licences", licenses);
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
                ", licences=" + licenses +
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

    public List<String> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<String> licenses) {
        this.licenses = licenses;
    }

    public List<HashMap<String, Object>> getReviews() {
        return reviews;
    }

    public void setReviews(List<HashMap<String, Object>> reviews) {
        this.reviews = reviews;
    }
}
