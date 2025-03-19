package com.example.service.impl;

import com.example.service.SubmissionService;
import com.example.model.MarkingRubric;
import com.example.model.MarkingRubricImage;
import com.example.model.Submission;
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
    
}
