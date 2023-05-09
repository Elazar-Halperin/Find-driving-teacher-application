package com.elazarhalperin.fluentify.Models;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class ChatModel {
    String id;
    String teacherUid;
    String studentUid;
    List<Map<String, Object>> messages;

    public ChatModel() {
    }

    public ChatModel(String teacherUid, String studentUid, List<Map<String, Object>> messages) {
        this.teacherUid = teacherUid;
        this.studentUid = studentUid;
        this.messages = messages;
        id = "";
    }

    public String getTeacherUid() {
        return teacherUid;
    }

    public void setTeacherUid(String teacherUid) {
        this.teacherUid = teacherUid;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Map<String, Object>> getMessages() {
        return messages;
    }

    public void setMessages(List<Map<String, Object>> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "ChatModel{" +
                "teacherUid='" + teacherUid + '\'' +
                ", studentUid='" + studentUid + '\'' +
                ", messages=" + messages +
                '}';
    }
}
