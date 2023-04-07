package com.elazarhalperin.fluentify.Models;

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
}
