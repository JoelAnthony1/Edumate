package com.example.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId; // Reference to Student Service

    private String rawResponse; // OCR-processed or direct text response
    private LocalDateTime submissionTime;

    // commented out as havent created ENUM stuff
    // @Enumerated(EnumType.STRING)
    // private SubmissionStatus status; // PENDING, GRADED, ERROR

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL)
    private GradingResult gradingResult;
}
