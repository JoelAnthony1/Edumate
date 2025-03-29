package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.MarkingRubric;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface MarkingRubricRepo extends JpaRepository<MarkingRubric, Long>{

       @Query("SELECT m FROM MarkingRubric m " +
           "WHERE m.classroomId = :classroomId " +
           "AND :studentId MEMBER OF m.studentIds")
       List<MarkingRubric> findByClassroomIdAndStudentId(@Param("classroomId") Long classroomId,
                                                      @Param("studentId") Long studentId);
       @Query("SELECT m FROM MarkingRubric m " +
           "WHERE m.classroomId = :classroomId ")
    List<MarkingRubric> findByClassroomId(@Param("classroomId") Long classroomId);
}
