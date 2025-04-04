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
import com.example.dto.ImageMetaDTO;
import java.util.stream.Collectors;



import com.example.model.MarkingRubric;
import com.example.model.MarkingRubricImage;
import com.example.service.MarkingRubricService;
import java.util.List;

@RestController
@RequestMapping("/rubrics")
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

    @PutMapping("/{rubricId}/upload-question-images")
    public ResponseEntity<MarkingRubric> uploadQuestionImagesToRubric(@PathVariable Long rubricId,
                                                                    @RequestParam("questionImages") List<MultipartFile> questionImages) {
        try {
            MarkingRubric updatedRubric = markingRubricService.addQuestionImagesToRubric(rubricId, questionImages);
            return ResponseEntity.ok(updatedRubric);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{rubricId}/image-metadata")
    public ResponseEntity<List<ImageMetaDTO>> getImageMetadata(@PathVariable Long rubricId) {
        List<MarkingRubricImage> images = markingRubricService.getImagesByRubricId(rubricId);
        List<ImageMetaDTO> metadata = images.stream()
            .map(img -> new ImageMetaDTO(
                img.getId(),
                "/rubrics/" + rubricId + "/images/" + img.getId()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(metadata);
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

    @PutMapping("/{rubricId}/extract-question-png")
    public ResponseEntity<MarkingRubric> extractQuestionsFromPNG(@PathVariable Long rubricId) {
        try {
            MarkingRubric updatedRubric = markingRubricService.extractQuestionsFromPNG(rubricId);
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

    @PostMapping("/{rubricId}/add-student")
    public ResponseEntity<MarkingRubric> addStudentToRubric(@PathVariable Long rubricId,
                                                               @RequestParam("studentId") long studentId) {
        try {
            MarkingRubric updatedRubric = markingRubricService.addStudentToRubric(rubricId, studentId);
            return ResponseEntity.ok(updatedRubric);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/classrooms/{classroomId}/students/{studentId}")
    public ResponseEntity<List<MarkingRubric>> getRubricsByStudentAndClass(
        @PathVariable Long studentId,
        @PathVariable Long classroomId
    ) {
        List<MarkingRubric> rubrics = markingRubricService.getRubricsByStudentAndClass(studentId, classroomId);
        return ResponseEntity.ok(rubrics);
    }

    // Added
    @GetMapping("/classrooms/{classroomId}")
    public ResponseEntity<List<MarkingRubric>> getRubricsByClassroom(@PathVariable Long classroomId) {
        List<MarkingRubric> rubrics = markingRubricService.getRubricsByClassroom(classroomId);
        return ResponseEntity.ok(rubrics);
    }
    

}
