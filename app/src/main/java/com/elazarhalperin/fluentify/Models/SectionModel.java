package com.elazarhalperin.fluentify.Models;

import java.util.List;

public class SectionModel {
    String name;
    List<TeacherModel> teacherList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TeacherModel> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<TeacherModel> teacherList) {
        this.teacherList = teacherList;
    }

    public SectionModel() {
    }

    public SectionModel(String name, List<TeacherModel> teacherList) {
        this.name = name;
        this.teacherList = teacherList;
    }
}
