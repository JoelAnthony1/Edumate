package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class GradingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment for PostgreSQL
    private Long id;

    @OneToOne
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission; // Ensures one-to-one relationship with Submission

    private Double score; // AI-generated score

    @Column(columnDefinition = "TEXT") // Use TEXT type to handle longer AI-generated feedback
    private String feedback;

    private Boolean validatedByBayesian = false; // Default value is false
}
