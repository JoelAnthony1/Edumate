package com.example.assignmentController;

import com.example.assignmentservice.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/extract-text")
    public ResponseEntity<String> extractText(@RequestParam("file") MultipartFile file) {
        String extractedText = assignmentService.extractTextFromImage(file);
        return ResponseEntity.ok(extractedText);
    }
}
