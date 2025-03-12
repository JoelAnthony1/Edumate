package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class MarkingRubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment for PostgreSQL
    private Long id;

    private String subject; // e.g., "Mathematics", "Science"


    @Column(columnDefinition = "TEXT") // Stores grading criteria as text
    private String gradingCriteria;

    // @Lob
    // @ElementCollection // This allows storing multiple images
    // @Column(name = "image_data", columnDefinition = "BYTEA")
    // private List<byte[]> images; // Store images in binary format

    @OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarkingRubricImage> images = new ArrayList<>();

}