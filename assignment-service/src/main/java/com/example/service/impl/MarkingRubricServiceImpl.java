package com.example.service.impl;

import com.example.model.MarkingRubric;
import com.example.model.MarkingRubricImage;
import com.example.repository.MarkingRubricRepo;
import com.example.service.MarkingRubricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

//AI import
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarkingRubricServiceImpl implements MarkingRubricService {

    private final MarkingRubricRepo markingRubricRepo;
    private final ChatClient chatClient;

    @Autowired
    public MarkingRubricServiceImpl(MarkingRubricRepo markingRubricRepo, ChatClient chatClient) {
        this.markingRubricRepo = markingRubricRepo;
        this.chatClient = chatClient;
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
            MarkingRubricImage rubricImage = new MarkingRubricImage();
            rubricImage.setImageData(image.getBytes());
            rubricImage.setRubric(rubric);
            rubric.getImages().add(rubricImage);
        }
        
        return markingRubricRepo.save(rubric);
    }

    @Override
    public MarkingRubric getMarkingRubricById(Long rubricId) {
        return markingRubricRepo.findById(rubricId)
            .orElseThrow(() -> new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImageData(Long rubricId, Long imageId) {
        MarkingRubric rubric = markingRubricRepo.findById(rubricId)
            .orElseThrow(() -> new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found"));
        MarkingRubricImage image = rubric.getImages().stream()
            .filter(img -> img.getId().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Image with ID " + imageId + " not found"));
        return image.getImageData();
    }

    @Override
    @Transactional
    public MarkingRubric extractAnswersFromPNG(Long rubricId) throws IOException {

        // Retrieve the rubric by ID
        MarkingRubric rubric = markingRubricRepo.findById(rubricId)
        .orElseThrow(() -> new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found"));
        
        //get images from marking_rubric
        List<MarkingRubricImage> images = rubric.getImages();
        if (images.isEmpty()) {
            throw new IllegalArgumentException("No images found for rubric ID " + rubricId);
        }

        // Convert each image's byte[] into a Media object
        List<Media> mediaList = images.stream()
            .map(img -> new Media(MimeTypeUtils.IMAGE_PNG, new ByteArrayResource(img.getImageData())))
            .collect(Collectors.toList());
        
        String inputMessage = """
            Extract all mathematical equations, transformations, and solutions in a structured text format that is optimized for machine readability. Ensure that:
            - Fractions are written using `/` (e.g., `5/3`).
            - Mixed fractions (e.g., `2 3/4`) should be written explicitly with spaces and not interpreted as exponentiation.
            - Absolute values are represented as `|x|`.
            - Square roots are written as `sqrt(x)`.
            - Powers are written using `^` (e.g., `x^2`).
            - Parentheses are **preserved exactly** as in the original image to ensure correct grouping.
            - Inequalities and equalities (`=`, `<`, `>`, `<=`, `>=`) are **accurately captured**.
            - Greek letters (`π`, `θ`, etc.) should be **preserved correctly**.
            - Trigonometric functions and inverse functions are preserved **without modification** (e.g., `sin^-1(x)`, `tan(θ)`).
            - Vertical fractions or summations should be structured correctly (e.g., `a/b` for fractions).
            - Multi-line equations should **maintain correct line breaks** and **should not be merged** into a single line.
            - Minus signs (`-`) must be **extracted correctly** and **not replaced** with Unicode variations.
            - No extra explanatory text or formatting is added—**only extract exactly what is present in the image**.
        """;

        // Create a UserMessage including all media objects
        var userMessage = new UserMessage(inputMessage, mediaList);
        
        // Build a Prompt and call the OpenAI API (using an injected chatClient)
        var prompt = new Prompt(List.of(userMessage));

        var responseSpec = chatClient
            .prompt(prompt)
            .options(OpenAiChatOptions.builder().build())
            .call();
        var chatResponse = responseSpec.chatResponse();
        String extractedAnswer = chatResponse.getResult().getOutput().getText();
        
        // Save the extracted answer into the rubric's gradingCriteria and persist
        rubric.setGradingCriteria(extractedAnswer);
        return markingRubricRepo.save(rubric);

    }

    @Override
    @Transactional
    public MarkingRubric deleteImageFromRubric(Long rubricId, Long imageId) {
        MarkingRubric rubric = markingRubricRepo.findById(rubricId)
            .orElseThrow(() -> new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found"));
        
        boolean removed = rubric.getImages().removeIf(img -> img.getId().equals(imageId));
        if (!removed) {
            throw new IllegalArgumentException("Image with ID " + imageId + " not found in rubric " + rubricId);
        }
        
        return markingRubricRepo.save(rubric);
    }


}