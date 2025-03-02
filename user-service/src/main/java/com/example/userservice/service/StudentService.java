package com.example.userservice.service;

import com.example.userservice.model.Student;
import java.util.List;

public interface StudentService {
    Student createStudent(Student student);
    Student getStudentById(Long id);
    List<Student> getAllStudents();
    List<Student> getStudentsByGrade(String grade);
    List<Student> getStudentsByMajor(String major);
    List<Student> getStudentsByYear(Integer yearOfStudy);
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);

    // New method to fetch student by email (for profile fetching)
    Student getStudentProfileByEmail(String email);
}
