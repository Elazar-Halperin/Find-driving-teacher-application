package com.elazarhalperin.fluentify.Models;

import java.util.List;

public class TeacherModel extends UserModel {
    float lessonPrice;
    float rating;
    String location;
    String info;
    String phoneNumber;
    List<String> licences;

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
