package com.elazarhalperin.fluentify.Models;

import java.io.Serializable;
import java.util.List;

public class CategoryModel implements Serializable {
    String name;
    List<TeacherModel> teachers;

    public CategoryModel(String name, List<TeacherModel> teachers) {
        this.name = name;
        this.teachers = teachers;
    }

    public CategoryModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TeacherModel> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherModel> teachers) {
        this.teachers = teachers;
    }
}
