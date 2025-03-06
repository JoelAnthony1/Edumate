package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class MarkingRubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment for PostgreSQL
    private Long id;

    private String subject; // e.g., "Mathematics", "Science"

    private String question; // The question this rubric applies to

    @Column(columnDefinition = "TEXT") // Stores grading criteria as structured text or JSON
    private String gradingCriteria;

    private Double maxScore; // Maximum possible score for the question
}
