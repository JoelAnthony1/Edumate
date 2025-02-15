package com.example.assignmentservice;

import com.example.assignmentEntity.Assignment;
import com.example.assignmentRepo.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.io.IOException;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private String encodeImageToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    public String extractTextFromImage(MultipartFile file) {
        try {
            String base64Image = encodeImageToBase64(file);
            OpenAiService openAiService = new OpenAiService(openAiApiKey);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-4-turbo")
                    .messages(List.of(
                            new ChatMessage("user", "Extract all text from this image:"),
                            new ChatMessage("user", "data:image/jpeg;base64," + base64Image)
                    ))
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            String extractedText = result.getChoices().get(0).getMessage().getContent();

            Assignment assignment = new Assignment();
            assignment.setExtractedText(extractedText);
            assignmentRepository.save(assignment);

            return extractedText;

        } catch (Exception e) {
            return "Error extracting text: " + e.getMessage();
        }
    }
}
