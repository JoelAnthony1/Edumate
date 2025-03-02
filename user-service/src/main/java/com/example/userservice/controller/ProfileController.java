package com.example.userservice.controller;

import com.example.userservice.model.Student;
import com.example.userservice.service.StudentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final StudentService studentService;

    public ProfileController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Student getProfile() {
        // Get the current logged-in user
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return studentService.getStudentProfileByEmail(loggedInUser.getUsername());  // Use the correct method name here
    }
}
