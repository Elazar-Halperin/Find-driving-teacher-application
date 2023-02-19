package com.elazarhalperin.fluentify.Models;

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
    boolean isPhoneVerified;

    public TeacherModel() {
    }


    public TeacherModel(String uid, String name, String email, String signUpDate, double lessonPrice, double rating, String location, String info, String phoneNumber, List<String> licences) {
        super(uid, name, email, signUpDate);
        this.lessonPrice = lessonPrice;
        this.rating = rating;
        this.location = location;
        this.info = info;
        this.phoneNumber = phoneNumber;
        this.licences = licences;
        isPhoneVerified = false;
    }

    public TeacherModel(Map<String, Object> mapTeacher) {
        uid = (String) mapTeacher.get("uid");
        name = (String) mapTeacher.get("name");
        email = (String) mapTeacher.get("email");
        signUpDate = (String) mapTeacher.get("signUpDate");
        this.lessonPrice = (double) mapTeacher.get("lessonPrice");
        this.rating = (double) mapTeacher.get("rating");
        this.location = (String) mapTeacher.get("location");
        this.info = (String) mapTeacher.get("info");
        this.phoneNumber = (String) mapTeacher.get("phoneNumber");
        this.licences = (List<String>) mapTeacher.get("licences");
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
}
