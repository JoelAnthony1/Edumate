package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Student extends User {
    private String grade;
    private String major;
    private Integer yearOfStudy;

    public Student() {
        super();
    }

    public Student(Long id, String name, String email, String password, UserRole role,
                  String grade, String major, Integer yearOfStudy) {
        super(id, name, email, password, role);
        this.grade = grade;
        this.major = major;
        this.yearOfStudy = yearOfStudy;
    }
} 