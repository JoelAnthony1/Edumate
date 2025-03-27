package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment for PostgreSQL
    private Long id;

    private Long classId;

    private Long studentId;

    private List<String> feedbackHistory;

    private String summary; // overall feedback for student; to be updated after each grading


    // getter
    public Long getId() {
        return this.id;
    }

    public Long getClassId() {
        return this.classId;
    }

    public Long getStudentId() {
        return this.studentId;
    }

    public List<String> getFeedbackHistory() {
        return this.feedbackHistory;
    }

    public String getSummary() {
        return this.summary;
    }



    // setter
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void addFeedbackToHistory(String feedback) {
        this.feedbackHistory.add(feedback);
    }

    public String setSummary(String summary) {
        this.summary = summary;
    }



}
