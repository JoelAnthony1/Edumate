package com.example.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.model.*;

public interface AnalysisService {
    // CRUD
    Analysis createAnalysis(Analysis analysis);
    Optional<Analysis> getAnalysisById(Long id);
    void deleteAnalysis(Long id);

    List<FeedbackHistory> addFeedbackToAnalysis(Long analysisId, String feedback);
    String createAnalysisSummary(Long analysisId, List<FeedbackHistory> allFeedbacks); 
}
