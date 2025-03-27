package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
public class FeedbackHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "analysis_id", nullable = false)  // Foreign key to Analysis
    @JsonBackReference  // Breaks the circular reference during JSON serialization
    private Analysis analysis;

    @Column(columnDefinition = "TEXT")
    private String feedback;  // Stores individual feedback entry


    // getter
    public String getFeedback() {
        return this.feedback;
    }
}
