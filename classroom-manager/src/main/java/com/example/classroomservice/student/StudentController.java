package com.example.classroomservice.student;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {
    private StudentService studentService;
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @ResponseBody
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentService.listStudents(); 
    }

    @ResponseBody
    @GetMapping("/students/{id}")
    public Student getStudent(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        if (student == null)
            throw new StudentNotFoundException(id);
        return studentService.getStudent(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @DeleteMapping("/students/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        if (studentService.getStudent(id) == null){
            throw new StudentNotFoundException(id);
        }
        studentService.deleteStudent(id);
    }

    @ResponseBody
    @PutMapping("/students/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student newStudentInfo) {
        Student student = studentService.updateStudent(id, newStudentInfo);
        if (student == null){
            throw new StudentNotFoundException(id);
        }
        return student;
    }
}
