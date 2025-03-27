package com.example.service.impl;

import com.example.service.SubmissionService;
import com.example.model.MarkingRubric;
import com.example.model.MarkingRubricImage;
import com.example.model.Submission;
import com.example.model.SubmissionImage;
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

    public List<String> addFeedbackToAnalysis(String feedback) {

    }

    public String createAnalysisSummary() {

    }


}
