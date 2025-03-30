package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Analysis;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

@Repository
public interface AnalysisRepo extends JpaRepository<Analysis, Long> {

    @Query("SELECT a FROM Analysis a WHERE a.classId = :classroomId AND a.studentId = :studentId")
    Optional<Analysis> findByClassIdAndStudentId(@Param("classroomId") Long classroomId, @Param("studentId") Long studentId);
}
