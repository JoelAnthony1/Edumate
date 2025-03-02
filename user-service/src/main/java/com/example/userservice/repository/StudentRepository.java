package com.example.userservice.repository;

import com.example.userservice.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByGrade(String grade);
    List<Student> findByMajor(String major);
    List<Student> findByYearOfStudy(Integer yearOfStudy);
    Optional<Student> findByEmail(String email); 
} 