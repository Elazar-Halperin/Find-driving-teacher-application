package com.elazarhalperin.fluentify.Models;

import java.io.Serializable;

public class UserModel implements Serializable {
    protected String uid;
    protected String name;
    protected String email;
    protected String signUpDate;

    public UserModel() {
    }

    public UserModel(String uid, String name, String email, String signUpDate) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.signUpDate = signUpDate;
    }

    public String getUid() {
        return uid;
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
