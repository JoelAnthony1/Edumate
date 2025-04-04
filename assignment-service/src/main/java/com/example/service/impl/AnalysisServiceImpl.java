package com.example.service.impl;

import com.example.service.AnalysisService;
import com.example.model.*;
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
import org.springframework.ai.content.Media;
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
import java.lang.*;

import org.springframework.web.multipart.MultipartFile;

import com.example.model.Analysis;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    
    private final AnalysisRepo analysisRepo;
    private final ChatClient chatClient;

    @Autowired
    public AnalysisServiceImpl(AnalysisRepo analysisRepo, ChatClient chatClient) {
        this.analysisRepo = analysisRepo;
        this.chatClient = chatClient;
    }

    @Override
    public Analysis createAnalysis(Analysis analysis) {
        return analysisRepo.save(analysis);
    }

    @Override
    public Optional<Analysis> getAnalysisById(Long id) {
        return analysisRepo.findById(id);
    }

    @Override
    @Transactional
    public void deleteAnalysis(Long id) {
        if (!analysisRepo.existsById(id)) {
            throw new RuntimeException("Analysis not found with id: " + id);
        }
        analysisRepo.deleteById(id);
    }

    @Override
    public List<FeedbackHistory> addFeedbackToAnalysis(Long analysisId, String feedback) {
        // Retrieve the analysis with its associated feedback
        Analysis analysis = analysisRepo.findById(analysisId)
            .orElseThrow(() -> new IllegalArgumentException("Analysis with ID " + analysisId + " not found"));
        
        analysis.addFeedbackToHistory(feedback);
        analysisRepo.save(analysis);

        return analysis.getFeedbackHistory();
    }
    @Override
    public Optional<Analysis> getAnalysisByClassAndStudent(Long classId, Long studentId) {
        return analysisRepo.findByClassIdAndStudentId(classId, studentId);
    }
    @Override
    public String createAnalysisSummary(Long analysisId, List<FeedbackHistory> allFeedbacks) {
        // Retrieve the analysis with its associated feedback
        Analysis analysis = analysisRepo.findById(analysisId)
            .orElseThrow(() -> new IllegalArgumentException("Analysis with ID " + analysisId + " not found"));

        // check if Analysis object has summary
        // if dun have:
        //      store feedback as initial summary
        String summary = analysis.getSummary();
        if (summary.isEmpty()) {
            summary = allFeedbacks.get(0).getFeedback();
            analysis.setSummary(summary);
        } else {
            // if have:
            //      get summary & all feedback and send to chatGPT
            //      update the summary in Analysis object to whats returned
            
            // Combine all feedback into a summary string
            String feedbackSummary = "\n\n'Additional Feedback from Analysis History:'\n";
            for (FeedbackHistory f : allFeedbacks) {
                feedbackSummary += "\n- " + f.getFeedback(); 
            }
        
            String inputMessage = summary + """
                \n\nUpdate the old summary of the student's learning above
                    based on the feedback from ALL of the student's past assignments,
                    the feedback of ALL the student's past assignment is stored below in
                    'Additional Feedback from Analysis History'. Also provide additional comments
                    where needed on: \n
                    - areas for improvement \n
                    - how to improve or practice \n
                    - what was done well \n
                    Return ONLY the updated summary \n\n
            """ + feedbackSummary;
        
            // Create a UserMessage including all media objects
            var userMessage = new UserMessage(inputMessage);
            
            // Build a Prompt and call the OpenAI API
            var prompt = new Prompt(List.of(userMessage));
            
            // calling ChatGPT
            var responseSpec = chatClient
                .prompt(prompt)
                .options(OpenAiChatOptions.builder().build())
                .call();
            var chatResponse = responseSpec.chatResponse();
            String extractedAnswer = chatResponse.getResult().getOutput().getText();
            summary = extractedAnswer;

            // Save the extracted answer into the analysis's Summary field and persist
            analysis.setSummary(extractedAnswer);
            analysisRepo.save(analysis);
        }

        return summary;
    }


}
