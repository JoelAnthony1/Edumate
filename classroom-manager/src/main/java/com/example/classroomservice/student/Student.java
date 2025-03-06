package com.example.classroomservice.student;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import com.example.classroomservice.classroom.Classroom;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "students")
    @JsonIgnore
    private Set<Classroom> classrooms = new HashSet<>();
}
