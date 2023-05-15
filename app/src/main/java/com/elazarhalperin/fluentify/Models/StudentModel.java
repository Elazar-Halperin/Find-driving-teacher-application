package com.elazarhalperin.fluentify.Models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentModel extends UserModel {
    int finishedLessons;

    public StudentModel(String uid, String name, String email, String signUpDate, int finishedLessons) {
        super(uid, name, email, signUpDate);
        this.finishedLessons = finishedLessons;
    }

    public int getFinishedLessons() {
        return finishedLessons;
    }

    public void setFinishedLessons(int finishedLessons) {
        this.finishedLessons = finishedLessons;
    }

    public StudentModel(Map<String, Object> mapTeacher) {
        super.uid = (String) mapTeacher.get("uid");
        super.name = (String) mapTeacher.get("name");
        super.email = (String) mapTeacher.get("email");
        super.signUpDate = (String) mapTeacher.get("signUpDate");
        this.finishedLessons = (int)( (long) mapTeacher.get("finishedLessons"));
    }



    @Override
    public String toString() {
        return "StudentModel{" +
                "finishedLessons=" + finishedLessons +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", signUpDate='" + signUpDate + '\'' +
                '}';
    }
}
