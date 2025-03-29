package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Submission;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudentIdAndClassroomIdAndMarkingRubricId(Long studentId, Long classroomId, Long markingRubricId);
}
