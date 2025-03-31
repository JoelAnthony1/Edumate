package com.example.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.model.MarkingRubric;
import com.example.model.MarkingRubricImage;


public interface MarkingRubricService {
    MarkingRubric createMarkingRubric(MarkingRubric markingRubric);
    List<MarkingRubricImage> getImagesByRubricId(Long rubricId);
    MarkingRubric addImagesToRubric(Long rubricId, List<MultipartFile> images) throws IOException;
    MarkingRubric addQuestionImagesToRubric(Long rubricId, List<MultipartFile> questionImages) throws IOException;
    MarkingRubric getMarkingRubricById(Long rubricId);
    byte[] getImageData(Long rubricId, Long imageId);
    MarkingRubric extractAnswersFromPNG(Long rubricId) throws IOException;
    MarkingRubric extractQuestionsFromPNG(Long rubricId) throws IOException;
    MarkingRubric deleteImageFromRubric(Long rubricId, Long imageId);
    MarkingRubric addDocumentToRubric(Long rubricId, MultipartFile document) throws IOException;
    MarkingRubric addDocumentToQuestion(Long rubricId, MultipartFile document) throws IOException;
    MarkingRubric addStudentToRubric(Long rubricId, long studentId);
    List<MarkingRubric> getRubricsByStudentAndClass(Long studentId, Long classroomId);
    List<MarkingRubric> getRubricsByClassroom(Long classroomId);
}
