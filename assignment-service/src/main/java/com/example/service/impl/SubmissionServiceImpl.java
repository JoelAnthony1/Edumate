package com.example.service.impl;

import com.example.service.*;
import com.example.model.MarkingRubric;
import com.example.model.MarkingRubricImage;
import com.example.model.Submission;
import com.example.model.SubmissionImage;
import com.example.repository.MarkingRubricRepo;
import com.example.repository.SubmissionRepo;
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

//PDF to PNG imports
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepo submissionRepo;
    private final ChatClient chatClient;
    private final AnalysisServiceImpl analysisServiceImpl;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepo submissionRepo, ChatClient chatClient) {
        this.submissionRepo = submissionRepo;
        this.chatClient = chatClient;
    }

    @Override
    public Submission createSubmission(Submission submission) {
        return submissionRepo.save(submission);
    }

    @Override
    public Optional<Submission> getSubmissionById(Long id) {
        return submissionRepo.findById(id);
    }

    @Override
    @Transactional
    public void deleteSubmission(Long id) {
        if (!submissionRepo.existsById(id)) {
            throw new RuntimeException("Submission not found with id: " + id);
        }
        submissionRepo.deleteById(id);
    }

    @Override
    @Transactional
    public Submission addImagesToSubmission(Long submissionId, List<MultipartFile> images) throws IOException {
        Optional<Submission> optionalSubmission = submissionRepo.findById(submissionId);

        if (optionalSubmission.isEmpty()) {
            throw new IllegalArgumentException("Submission with ID " + submissionId + " not found");
        }

        Submission submission = optionalSubmission.get();

        if (submission.getImages() == null) {
            submission.setImages(new ArrayList<>());
        }

        for (MultipartFile image : images) {
            SubmissionImage submissionImage = new SubmissionImage();
            submissionImage.setImageData(image.getBytes());
            submissionImage.setSubmission(submission);
            submission.getImages().add(submissionImage);
        }

        return submissionRepo.save(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImageData(Long submissionId, Long imageId) {
        Submission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission with ID " + submissionId + " not found"));
        
        SubmissionImage image = submission.getImages().stream()
            .filter(img -> img.getId().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Image with ID " + imageId + " not found"));
        
        return image.getImageData();
    }

    @Override
    @Transactional
    public Submission deleteImageFromSubmission(Long submissionId, Long imageId) {
        Submission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission with ID " + submissionId + " not found"));

        boolean removed = submission.getImages().removeIf(img -> img.getId().equals(imageId));
        
        if (!removed) {
            throw new IllegalArgumentException("Image with ID " + imageId + " not found in submission " + submissionId);
        }

        return submissionRepo.save(submission);
    }

    @Override
    @Transactional
    public Submission extractAnswersFromPNG(Long submissionId) throws IOException {
        // Retrieve the submission by ID
        Submission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission with ID " + submissionId + " not found"));

        // Get images from the submission
        List<SubmissionImage> images = submission.getImages();
        if (images.isEmpty()) {
            throw new IllegalArgumentException("No images found for submission ID " + submissionId);
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

        // Build a Prompt and call the OpenAI API
        var prompt = new Prompt(List.of(userMessage));

        // calling ChatGPT
        var responseSpec = chatClient
            .prompt(prompt)
            .options(OpenAiChatOptions.builder().build())
            .call();
        var chatResponse = responseSpec.chatResponse();
        String extractedAnswer = chatResponse.getResult().getOutput().getText();

        // Save the extracted answer into the submission's writtenAnswer field and persist
        submission.setWrittenAnswer(extractedAnswer);
        return submissionRepo.save(submission);
    }

    @Override
    @Transactional
    public Submission gradeSubmission(Long submissionId, Long analysisId) {
        // Retrieve the submission with its associated rubric
        Submission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission with ID " + submissionId + " not found"));
        
        // Ensure submission has an associated rubric
        if (submission.getMarkingRubric() == null) {
            throw new IllegalArgumentException("Submission must have an associated MarkingRubric");
        }
        
        // Combine grading criteria and the written answer into a grading prompt
        String gradingCriteria = submission.getMarkingRubric().getGradingCriteria();
        String writtenAnswer = submission.getWrittenAnswer();
        String promptText = String.format(
            "Based on the following grading criteria: %s, evaluate the following submission: %s and provide detailed feedback.",
            gradingCriteria, writtenAnswer);
        
        // Create a user message and build the prompt
        var userMessage = new UserMessage(promptText);
        var prompt = new Prompt(List.of(userMessage));
        
        // Call the ChatGPT API using ChatClient
        var responseSpec = chatClient.prompt(prompt)
            .options(OpenAiChatOptions.builder().build())
            .call();
        
        String feedback = responseSpec.chatResponse().getResult().getOutput().getText();
        // add feedback to Analysis object
        List<String> allFeedbacks = analysisServiceImpl.addFeedbackToAnalysis(feedback);
        // update Summary for latest feedback
        analysisServiceImpl.createAnalysisSummary(allFeedbacks);


        submission.setFeedback(feedback);
        
        return submissionRepo.save(submission);
    }





    
}
