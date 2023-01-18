package com.elazarhalperin.fluentify.Models;

public class UserModel {
    String name;
    String email;
    String signUpDate;

    public UserModel(String name, String email, String signUpDate) {
        this.name = name;
        this.email = email;
        this.signUpDate = signUpDate;
    }

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }


    public String getSignUpDate() {
        return signUpDate;
    }

}
