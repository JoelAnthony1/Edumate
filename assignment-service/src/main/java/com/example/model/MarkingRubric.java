package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
public class MarkingRubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment for PostgreSQL
    private Long id;

    private String subject; // e.g., "Mathematics", "Science"


    @Column(columnDefinition = "TEXT") // Stores grading criteria as text
    private String gradingCriteria;

    @OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // Exclude images from JSON responses
    private List<MarkingRubricImage> images = new ArrayList<>();

}