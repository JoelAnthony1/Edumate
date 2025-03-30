package com.example.classroomservice.classroom;

import java.util.Optional;
import java.util.*;
import com.example.classroomservice.student.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long>{
    List<Classroom> findByUserId(Long userId);
    @Query("SELECT c.students FROM Classroom c WHERE c.id = :id")
    Set<Student> findStudentsByClassroomId(@Param("id") Long id);

}