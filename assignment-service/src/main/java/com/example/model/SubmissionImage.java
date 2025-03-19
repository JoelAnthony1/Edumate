package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
public class SubmissionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image_data")
    @JsonIgnore  // prevents serialization of large binary data
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    @JsonBackReference  // breaks the circular reference during JSON serialization
    private Submission submission;
}
