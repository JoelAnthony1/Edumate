package com.example.model;

import jakarta.persistence.*;
import lombok.Data;
import com.example.model.*;
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

    // One-to-many relationship between Analysis and FeedbackHistory
    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackHistory> feedbackHistory = new ArrayList<>();


    @Column(columnDefinition = "TEXT") // Use TEXT type to handle longer AI-generated feedback
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

    public List<FeedbackHistory> getFeedbackHistory() {
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

    // Convenience method to add feedback to history
    public void addFeedbackToHistory(String feedback) {
        FeedbackHistory feedbackEntry = new FeedbackHistory();
        feedbackEntry.setAnalysis(this);
        feedbackEntry.setFeedback(feedback);
        this.feedbackHistory.add(feedbackEntry);
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }



}
