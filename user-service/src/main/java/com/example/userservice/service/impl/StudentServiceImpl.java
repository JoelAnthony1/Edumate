package com.example.userservice.service.impl;

import com.example.userservice.model.Student;
import com.example.userservice.service.StudentService;
import com.example.userservice.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getStudentsByGrade(String grade) {
        return studentRepository.findByGrade(grade);
    }

    @Override
    public List<Student> getStudentsByMajor(String major) {
        return studentRepository.findByMajor(major);
    }

    @Override
    public List<Student> getStudentsByYear(Integer yearOfStudy) {
        return studentRepository.findByYearOfStudy(yearOfStudy);
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        Student existingStudent = getStudentById(id);
        existingStudent.setGrade(student.getGrade());
        existingStudent.setMajor(student.getMajor());
        existingStudent.setYearOfStudy(student.getYearOfStudy());
        existingStudent.setName(student.getName());
        existingStudent.setEmail(student.getEmail());
        return studentRepository.save(existingStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // New method to fetch student by email
    @Override
    public Student getStudentProfileByEmail(String email) {
        Optional<Student> studentOptional = studentRepository.findByEmail(email);
        return studentOptional.orElseThrow(() -> new RuntimeException("Student not found with email: " + email));
    }
}
