package com.example.classroomservice.classroom;

import java.util.Optional;
import java.util.List; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long>{
    List<Classroom> findByUserId(Long userId);
}
