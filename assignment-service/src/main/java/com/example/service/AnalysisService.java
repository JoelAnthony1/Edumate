package com.example.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.model.Submission;

public interface AnalysisService {
    List<String> addFeedbackToAnalysis(Long analysisId, String feedback);
    String createAnalysisSummary(Long analysisId, List<String> allFeedbacks); 
}
