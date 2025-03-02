package com.example.userservice.service.impl;

import com.example.userservice.model.Teacher;
import com.example.userservice.repository.TeacherRepository;
import com.example.userservice.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public List<Teacher> getTeachersBySubject(String subject) {
        return teacherRepository.findBySubject(subject);
    }

    @Override
    public List<Teacher> getTeachersByQualification(String qualification) {
        return teacherRepository.findByQualification(qualification);
    }

    @Override
    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = getTeacherById(id);
        teacher.setName(teacherDetails.getName());
        teacher.setEmail(teacherDetails.getEmail());
        teacher.setSubject(teacherDetails.getSubject());
        teacher.setQualification(teacherDetails.getQualification());
        teacher.setExperience(teacherDetails.getExperience());
        return teacherRepository.save(teacher);
    }

    @Override
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new EntityNotFoundException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }
} 