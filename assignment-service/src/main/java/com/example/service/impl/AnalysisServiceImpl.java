package com.example.service.impl;

import com.example.service.SubmissionService;
import com.example.model.MarkingRubric;
import com.example.model.MarkingRubricImage;
import com.example.model.Submission;
import com.example.model.SubmissionImage;
import com.example.repository.MarkingRubricRepo;
import com.example.repository.AnalysisRepo;
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


import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.model.Submission;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    
    private final AnalysisRepo analysisRepo;
    private final ChatClient chatClient;

    @Autowired
    public analysisServiceImpl(AnalysisRepo analysisRepo, ChatClient chatClient) {
        this.analysisRepo = analysisRepo;
        this.chatClient = chatClient;
    }

    public List<String> addFeedbackToAnalysis(Long analysisId, String feedback) {
        // Retrieve the analysis with its associated feedback
        Analysis analysis = analysisRepo.findById(analysisId)
            .orElseThrow(() -> new IllegalArgumentException("Analysis with ID " + analysisId + " not found"));
        
        analysis.addFeedbackToHistory(feedback);
        analysisRepo.save(analysis);

        return analysis.getFeedbackHistory();
    }

    public String createAnalysisSummary() {
        // check if Analysis object has summary
        // if have:
        //      get summary and send to chatGPT
        //      update the summary in Analysis object to whats returned

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

        // if dun have:
        //      store feedback as initial summary

        return summary;
    }


}
