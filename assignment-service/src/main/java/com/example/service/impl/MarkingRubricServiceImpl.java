package com.example.service.impl;

import com.example.model.MarkingRubric;
import com.example.repository.MarkingRubricRepo;
import com.example.service.MarkingRubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MarkingRubricServiceImpl implements MarkingRubricService {

    private final MarkingRubricRepo markingRubricRepo;

    @Autowired
    public MarkingRubricServiceImpl(MarkingRubricRepo markingRubricRepo) {
        this.markingRubricRepo = markingRubricRepo;
    }

    @Override
    public MarkingRubric createMarkingRubric(MarkingRubric markingRubric) {
        return markingRubricRepo.save(markingRubric);
    }

    /**
     * Adds multiple images to an existing MarkingRubric object.
     * @param rubricId The ID of the MarkingRubric.
     * @param images List of MultipartFile images to be added.
     * @return The updated MarkingRubric object.
     * @throws IOException If there is an issue reading the image bytes.
     * @throws IllegalArgumentException If the MarkingRubric is not found.
     */
    @Override
    @Transactional
    public MarkingRubric addImagesToRubric(Long rubricId, List<MultipartFile> images) throws IOException {
        Optional<MarkingRubric> optionalRubric = markingRubricRepo.findById(rubricId);
        
        if (optionalRubric.isEmpty()) {
            throw new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found");
        }
        
        MarkingRubric rubric = optionalRubric.get();
        
        if (rubric.getImages() == null) {
            rubric.setImages(new ArrayList<>());
        }
        
        for (MultipartFile image : images) {

            // System.out.println("**************** DEBUG **********************");
            // byte[] imageBytes = image.getBytes();
            // System.out.println("Type of image.getBytes(): " + imageBytes.getClass().getName());

            rubric.getImages().add(image.getBytes());
        }
        
        return markingRubricRepo.save(rubric);
    }

}
