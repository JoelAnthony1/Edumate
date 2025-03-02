package com.example.userservice.controller;

import com.example.userservice.model.Student;
import com.example.userservice.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Create a new student
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    // Get a specific student's profile by their ID (admin or teacher role required)
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    // Get all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // Get students by grade
    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<Student>> getStudentsByGrade(@PathVariable String grade) {
        return ResponseEntity.ok(studentService.getStudentsByGrade(grade));
    }

    // Get students by major
    @GetMapping("/major/{major}")
    public ResponseEntity<List<Student>> getStudentsByMajor(@PathVariable String major) {
        return ResponseEntity.ok(studentService.getStudentsByMajor(major));
    }

    // Get students by year of study
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Student>> getStudentsByYear(@PathVariable Integer year) {
        return ResponseEntity.ok(studentService.getStudentsByYear(year));
    }

    // Update student details by ID
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return ResponseEntity.ok(studentService.updateStudent(id, student));
    }

    // Delete student by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    // Get the profile of the logged-in student (uses authenticated user from Spring Security)
    @GetMapping("/profile")
    public ResponseEntity<Student> getProfile() {
        // Get the currently logged-in user
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = loggedInUser.getUsername(); // Assuming email is used as the username
        Student student = studentService.getStudentProfileByEmail(email);
        
        return ResponseEntity.ok(student);
    }
}
