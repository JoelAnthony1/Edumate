package com.example.classroomservice.classroom;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import com.example.classroomservice.student.Student;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID
    private Long id;
    private String classname;
    private int capacity;
    @ManyToMany
    @JoinTable(
        name = "classroom_student",
        joinColumns = @JoinColumn(name = "classroom_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students = new HashSet<>();
}
