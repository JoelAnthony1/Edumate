package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Teacher extends User {
    private String subject;
    private String qualification;
    private String experience;

    public Teacher() {
        super();
    }

    public Teacher(Long id, String name, String email, String password, UserRole role,
                  String subject, String qualification, String experience) {
        super(id, name, email, password, role);
        this.subject = subject;
        this.qualification = qualification;
        this.experience = experience;
    }
} 