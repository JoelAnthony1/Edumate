package com.example.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.model.MarkingRubric;

public interface MarkingRubricService {
    MarkingRubric createMarkingRubric(MarkingRubric markingRubric);
    MarkingRubric addImagesToRubric(Long rubricId, List<MultipartFile> images) throws IOException;
}
