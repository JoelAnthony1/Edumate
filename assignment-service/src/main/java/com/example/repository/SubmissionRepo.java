package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Submission;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, Long> {

}
