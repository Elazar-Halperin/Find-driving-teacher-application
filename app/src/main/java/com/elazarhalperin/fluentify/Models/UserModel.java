package com.elazarhalperin.fluentify.Models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserModel implements Serializable {
    protected String uid;
    protected String name;
    protected String email;
    protected String signUpDate;
    protected String signUpDate_he;

    public UserModel() {
    }

    public UserModel(String uid, String name, String email, String signUpDate, String signUpDate_he) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.signUpDate = signUpDate;
        this.signUpDate_he = signUpDate_he;
    }

    public String getSignUpDate_he() {
        return signUpDate_he;
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

    public void setEmail(String email) {
        this.email = email;
    }

    // get the user as map
    // so we can update it better in the firebase
    public Map<String, Object> getMap() {
        Map<String, Object> userModel = new HashMap<>();
        userModel.put("uid", uid);
        userModel.put("name", name);
        userModel.put("email", email);
        userModel.put("signUpDate", signUpDate);
        userModel.put("signUpDate_he",signUpDate);

        return userModel;
    }

}
