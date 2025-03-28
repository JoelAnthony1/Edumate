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

    private Long classroomId; 

    @ElementCollection
    @CollectionTable(name = "marking_rubric_student_ids", joinColumns = @JoinColumn(name = "rubric_id"))
    @Column(name = "student_id")
    private List<Long> studentIds = new ArrayList<>();

    private String title; // e.g., "KRSS P1", "xx"

    @Column(columnDefinition = "TEXT") // Stores questions as text
    private String questions;

    @Column(columnDefinition = "TEXT") // Stores grading criteria as text
    private String gradingCriteria;

    @OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // Exclude images from JSON responses
    private List<MarkingRubricImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // Exclude question images from JSON responses
    private List<MarkingRubricImage> questionImages = new ArrayList<>();

    /**
     * Adds a student ID to the list of student IDs.
     *
     * @param studentId the student ID to add
     */
    public void addStudentToRubric(long studentId) {
            this.studentIds.add(studentId);
        }

}