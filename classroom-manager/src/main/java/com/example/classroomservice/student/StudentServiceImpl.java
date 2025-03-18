package com.example.classroomservice.student;
import java.util.*;

import org.springframework.stereotype.Service;
import com.example.classroomservice.exception.*;

@Service
public class StudentServiceImpl implements StudentService{
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository){
        this.studentRepository=studentRepository;
    }

    @Override
    public List<Student> listStudents(){
        return studentRepository.findAll();
    }
    @Override
    public Student getStudent(Long id) {
        return studentRepository.findById(id).orElse(null); 
    }

    @Override
    public Student addStudent(Student student){
        Optional<Student> existingStudent = studentRepository.findByName(student.getName());
        if (existingStudent.isPresent()) {
            throw new IllegalArgumentException("Student with the name " + student.getName() + " already exists");
        }
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Long id, Student newStudentInfo) {
        return studentRepository.findById(id).map(student -> {
            student.setName(newStudentInfo.getName());
            return studentRepository.save(student);
        }).orElse(null);
    }
    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
