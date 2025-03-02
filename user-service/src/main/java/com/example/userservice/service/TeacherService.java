package com.example.userservice.service;

import com.example.userservice.model.Teacher;
import java.util.List;

public interface TeacherService {
    Teacher createTeacher(Teacher teacher);
    Teacher getTeacherById(Long id);
    List<Teacher> getAllTeachers();
    List<Teacher> getTeachersBySubject(String subject);
    List<Teacher> getTeachersByQualification(String qualification);
    Teacher updateTeacher(Long id, Teacher teacher);
    void deleteTeacher(Long id);
} 