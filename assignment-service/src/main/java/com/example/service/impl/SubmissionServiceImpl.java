package com.example.service.impl;

import com.example.service.SubmissionService;
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
            Extract all questions number and the student's answers in a structured text format optimized for machine readability.
            Format the output in Q&A pairs, where each question starts with "Question Number:" followed by the question number, and each answer starts with "Student Answer:" followed by the student's written answer.
            
            For example:
            Question Number: 2)a)
            Student Answer: Normal contact force by the right support on the plank.
            
            Extract only the content present in the image without additional commentary.
        """;

        // Create a UserMessage including all media objects
        var userMessage = new UserMessage(inputMessage, mediaList);

        // Build a Prompt and call the OpenAI API
        var prompt = new Prompt(List.of(userMessage));

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
    public Submission gradeSubmission(Long submissionId) {
        // Retrieve the submission with its associated rubric
        Submission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission with ID " + submissionId + " not found"));
        
        // Ensure submission has an associated rubric
        if (submission.getMarkingRubric() == null) {
            throw new IllegalArgumentException("Submission must have an associated MarkingRubric");
        }
        
        // Retrieve the questions, grading criteria, and student's answer
        String questions = submission.getMarkingRubric().getQuestions();
        String gradingCriteria = submission.getMarkingRubric().getGradingCriteria();
        String writtenAnswer = submission.getWrittenAnswer();
        
        // Build the detailed marking prompt for a question-by-question evaluation
        String markingPromptText = String.format(
            "Mark the student's submission strictly by referring to the provided grading criteria. For each question, compare the student's answer with the expected answer given in the grading criteria and provide brief marking comments only if the student's answer does not the criteria. Do not include any independent analysis, overall summary, or final mark.\n\n" +
            "Grading Criteria:\n%s\n\n" +
            "Student's Submission:\n%s\n\n" +
            "Provide detailed feedback for each question only.",
            gradingCriteria, writtenAnswer);
        
        // Create a user message with the marking prompt and build the prompt object
        var userMessage = new UserMessage(markingPromptText);
        var prompt = new Prompt(List.of(userMessage));
        
        // Call the OpenAI API to get the feedback
        var responseSpec = chatClient.prompt(prompt)
            .options(OpenAiChatOptions.builder().model("gpt-4").build())
            .call();
        String feedback = responseSpec.chatResponse().getResult().getOutput().getText();
        
        // Save the generated feedback in the submission and persist
        submission.setFeedback(feedback);
        return submissionRepo.save(submission);
    }

    @Override
    @Transactional
    public Submission markAsSubmitted(Long submissionId) {
        Submission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission with ID " + submissionId + " not found"));
        submission.setSubmitted(true);
        return submissionRepo.save(submission);
    }

    @Override
    @Transactional
    public Submission markAsGraded(Long submissionId) {
        Submission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission with ID " + submissionId + " not found"));
        submission.setGraded(true);
        return submissionRepo.save(submission);
    }

    @Override
    public String getFeedbackForStudentAndClassroomAndRubric(Long studentId, Long classroomId, Long markingRubricId) {
        Submission submission = submissionRepo.findByStudentIdAndClassroomIdAndMarkingRubricId(studentId, classroomId, markingRubricId)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found for studentId " + studentId 
                 + ", classroomId " + classroomId + ", and markingRubricId " + markingRubricId));
        return submission.getFeedback();
    }
    
}
