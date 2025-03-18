package com.example.classroomservice.student;

import java.util.List;

public interface StudentService {
    List<Student> listStudents();
    Student getStudent(Long id);
    Student addStudent(Student student);
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);
}
