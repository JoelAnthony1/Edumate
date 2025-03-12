package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class MarkingRubricImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "marking_rubric_id")
    private MarkingRubric rubric;
}
