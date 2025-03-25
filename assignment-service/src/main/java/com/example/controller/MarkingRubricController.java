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
import com.example.model.MarkingRubricImage;
import com.example.service.MarkingRubricService;
import java.util.List;

@RestController
@RequestMapping("/api/rubrics")
public class MarkingRubricController {

    private final MarkingRubricService markingRubricService;

    @Autowired
    public MarkingRubricController(MarkingRubricService markingRubricService) {
        this.markingRubricService = markingRubricService;
    }

    // Create a new marking rubric
    @PostMapping
    public ResponseEntity<MarkingRubric> createMarkingRubric(@RequestBody MarkingRubric markingRubric) {
        return ResponseEntity.ok(markingRubricService.createMarkingRubric(markingRubric));
    }

    @DeleteMapping("/{rubricId}/images/{imageId}")
    public ResponseEntity<MarkingRubric> deleteImage(@PathVariable Long rubricId, @PathVariable Long imageId) {
        try {
            MarkingRubric updatedRubric = markingRubricService.deleteImageFromRubric(rubricId, imageId);
            return ResponseEntity.ok(updatedRubric);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * API Endpoint to upload multiple images for a specific MarkingRubric.
     * @param rubricId The ID of the MarkingRubric.
     * @param images List of MultipartFile images uploaded.
     * @return ResponseEntity with the updated MarkingRubric.
     */
    @PutMapping("/{rubricId}/upload-images")
    public ResponseEntity<MarkingRubric> uploadImagesToRubric(@PathVariable Long rubricId,
                                                              @RequestParam("images") List<MultipartFile> images) {
        try {
            MarkingRubric updatedRubric = markingRubricService.addImagesToRubric(rubricId, images);
            return ResponseEntity.ok(updatedRubric);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{rubricId}")
    public ResponseEntity<MarkingRubric> getMarkingRubric(@PathVariable Long rubricId) {
        try {
            MarkingRubric rubric = markingRubricService.getMarkingRubricById(rubricId);
            return ResponseEntity.ok(rubric);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{rubricId}/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long rubricId, @PathVariable Long imageId) {
        byte[] imageData = markingRubricService.getImageData(rubricId, imageId);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(imageData);
    }

    @PutMapping("/{rubricId}/extractPNG")
    public ResponseEntity<MarkingRubric> extractAnswersFromPNG(@PathVariable Long rubricId) {
        try {
            MarkingRubric updatedRubric = markingRubricService.extractAnswersFromPNG(rubricId);
            return ResponseEntity.ok(updatedRubric);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{rubricId}/upload-documents")
    public ResponseEntity<MarkingRubric> uploadDocumentToRubric(@PathVariable Long rubricId,
                                                                  @RequestParam("document") MultipartFile document) {
        try {
            MarkingRubric updatedRubric = markingRubricService.addDocumentToRubric(rubricId, document);
            return ResponseEntity.ok(updatedRubric);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }




}
