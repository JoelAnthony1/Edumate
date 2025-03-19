package com.example.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.model.Submission;

public interface SubmissionService {
    Submission createSubmission(Submission submission);
    Optional<Submission> getSubmissionById(Long id);
    void deleteSubmission(Long id);
}
