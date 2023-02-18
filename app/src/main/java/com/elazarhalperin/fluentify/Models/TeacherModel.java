package com.elazarhalperin.fluentify.Models;

import java.util.List;

public class TeacherModel extends UserModel {
    float lessonPrice;
    float rating;
    String location;
    String info;
    String phoneNumber;
    List<String> licences;

    public TeacherModel() {
    }


    public TeacherModel(String uid, String name, String email, String signUpDate, float lessonPrice, float rating, String location, String info, String phoneNumber, List<String> licences) {
        super(uid, name, email, signUpDate);
        this.lessonPrice = lessonPrice;
        this.rating = rating;
        this.location = location;
        this.info = info;
        this.phoneNumber = phoneNumber;
        this.licences = licences;
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
