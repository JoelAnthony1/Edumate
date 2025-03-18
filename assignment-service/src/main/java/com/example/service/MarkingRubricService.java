package com.example.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.model.MarkingRubric;

public interface MarkingRubricService {
    MarkingRubric createMarkingRubric(MarkingRubric markingRubric);
    MarkingRubric addImagesToRubric(Long rubricId, List<MultipartFile> images) throws IOException;
    MarkingRubric getMarkingRubricById(Long rubricId);
    byte[] getImageData(Long rubricId, Long imageId);
    MarkingRubric extractAnswersFromPNG(Long rubricId) throws IOException;
    MarkingRubric deleteImageFromRubric(Long rubricId, Long imageId);
}
