package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId; // Reference to student
    private Long classroomId; // Reference to classroom
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "marking_rubric_id", nullable = false)
    private MarkingRubric markingRubric;

    private Boolean submitted = false;
    private Boolean graded = false;

    @Column(columnDefinition = "TEXT") // Use TEXT type to handle longer AI extracted answer
    private String writtenAnswer;

    // commented out as havent created ENUM stuff
    // @Enumerated(EnumType.STRING)
    // private SubmissionStatus status; // PENDING, GRADED, ERROR

    private Double score;

    @Column(columnDefinition = "TEXT") // Use TEXT type to handle longer AI-generated feedback
    private String feedback;

    private Boolean validatedByBayesian = false; // Default value is false

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // Exclude images from JSON responses
    private List<SubmissionImage> images = new ArrayList<>();
}

