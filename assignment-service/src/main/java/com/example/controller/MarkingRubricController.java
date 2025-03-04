package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.MarkingRubric;
import com.example.service.MarkingRubricService;

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

}
