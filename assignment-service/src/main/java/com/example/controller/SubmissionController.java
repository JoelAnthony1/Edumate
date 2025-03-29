package com.example.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.example.model.MarkingRubric;
import com.example.model.Submission;
import com.example.service.MarkingRubricService;
import com.example.service.SubmissionService;

import java.util.List;

@RestController
@RequestMapping("/submission")
public class SubmissionController {

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // Create a new submission
    @PostMapping
    public ResponseEntity<Submission> createSubmission(@RequestBody Submission submission) {
        return ResponseEntity.ok(submissionService.createSubmission(submission));
    }

    // Retrieve a submission by ID
    @GetMapping("/{id}")
    public ResponseEntity<Submission> getSubmission(@PathVariable Long id) {
        return submissionService.getSubmissionById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a submission by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        try {
            submissionService.deleteSubmission(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

        /**
     * API Endpoint to upload multiple images for a specific Submission.
     * @param submissionId The ID of the Submission.
     * @param images List of MultipartFile images uploaded.
     * @return ResponseEntity with the updated Submission.
     */
    @PutMapping("/{submissionId}/upload-images")
    public ResponseEntity<Submission> uploadImagesToSubmission(@PathVariable Long submissionId,
                                                               @RequestParam("images") List<MultipartFile> images) {
        try {
            Submission updatedSubmission = submissionService.addImagesToSubmission(submissionId, images);
            return ResponseEntity.ok(updatedSubmission);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{submissionId}/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long submissionId, @PathVariable Long imageId) {
        byte[] imageData = submissionService.getImageData(submissionId, imageId);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(imageData);
    }

    @DeleteMapping("/{submissionId}/images/{imageId}")
    public ResponseEntity<Submission> deleteImage(@PathVariable Long submissionId, @PathVariable Long imageId) {
        try {
            Submission updatedSubmission = submissionService.deleteImageFromSubmission(submissionId, imageId);
            return ResponseEntity.ok(updatedSubmission);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{submissionId}/extractPNG")
    public ResponseEntity<Submission> extractAnswersFromPNG(@PathVariable Long submissionId) {
        try {
            Submission updatedSubmission = submissionService.extractAnswersFromPNG(submissionId);
            return ResponseEntity.ok(updatedSubmission);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{submissionId}/grade")
    public ResponseEntity<Submission> gradeSubmission(@PathVariable Long submissionId) {
        try {
            Submission gradedSubmission = submissionService.gradeSubmission(submissionId);
            return ResponseEntity.ok(gradedSubmission);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/{submissionId}/mark-submitted")
    public ResponseEntity<Submission> markSubmissionAsSubmitted(@PathVariable Long submissionId) {
        try {
            Submission updatedSubmission = submissionService.markAsSubmitted(submissionId);
            return ResponseEntity.ok(updatedSubmission);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{submissionId}/mark-graded")
    public ResponseEntity<Submission> markSubmissionAsGraded(@PathVariable Long submissionId) {
        try {
            Submission updatedSubmission = submissionService.markAsGraded(submissionId);
            return ResponseEntity.ok(updatedSubmission);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/feedback")
    public ResponseEntity<String> getFeedbackForStudentAndClassroomAndRubric(
            @RequestParam Long studentId,
            @RequestParam Long classroomId,
            @RequestParam Long markingRubricId) {
        try {
            String feedback = submissionService.getFeedbackForStudentAndClassroomAndRubric(studentId, classroomId, markingRubricId);
            return ResponseEntity.ok(feedback);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}