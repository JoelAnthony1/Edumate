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
import org.springframework.ai.content.Media;
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
    @Transactional
    public MarkingRubric addQuestionImagesToRubric(Long rubricId, List<MultipartFile> questionImages) throws IOException {
        Optional<MarkingRubric> optionalRubric = markingRubricRepo.findById(rubricId);
        if (optionalRubric.isEmpty()) {
            throw new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found");
        }
        MarkingRubric rubric = optionalRubric.get();

        if (rubric.getQuestionImages() == null) {
            rubric.setQuestionImages(new ArrayList<>());
        }

        for (MultipartFile image : questionImages) {
            MarkingRubricImage questionImage = new MarkingRubricImage();
            questionImage.setImageData(image.getBytes());
            questionImage.setRubric(rubric);
            rubric.getQuestionImages().add(questionImage);
        }
        return markingRubricRepo.save(rubric);
    }

    @Override
    public MarkingRubric getMarkingRubricById(Long rubricId) {
        return markingRubricRepo.findById(rubricId)
            .orElseThrow(() -> new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found"));
    }

    @Override
    @Transactional
    public byte[] getImageData(Long rubricId, Long imageId) {
        MarkingRubric rubric = markingRubricRepo.findById(rubricId)
            .orElseThrow(() -> new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found"));
        MarkingRubricImage image = rubric.getImages().stream()
            .filter(img -> img.getId().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Image with ID " + imageId + " not found"));
        // Force initialization of the blob within the transaction:
        byte[] data = image.getImageData();
        return data;
    }  

    @Override
    @Transactional(readOnly = true)
    public List<MarkingRubricImage> getImagesByRubricId(Long rubricId) {
        MarkingRubric rubric = markingRubricRepo.findById(rubricId)
            .orElseThrow(() -> new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found"));
    // Force initialization of the images collection:
        int size = rubric.getImages().size();
        return rubric.getImages();
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
            Instruction: You are given an image of a rubric table titled “CONTENT,” which has rows labeled Quality of Response, Use of Illustration, Relevance, and Overall. Each row contains five different bands (Band 1 to Band 5) with descriptions.

            Task: Please extract all the text from the table—every descriptor, band, and explanation—and present it as structured text. Use headings for each descriptor (e.g., "Quality of Response") and within each heading, list each band (e.g., "Band 1: ...", "Band 2: ..."). Make sure to capture all the details of the text in each cell, without omissions or paraphrasing. Maintain the same order as in the image.

            Desired Output:

            A plain-text version of the table where each row is broken down by its descriptor.

            For each descriptor, include five sub-sections labeled “Band 1” through “Band 5.”

            Each sub-section should contain the exact text found in the corresponding cell of the table.

            Do not add commentary or summary; just provide the raw extracted text.
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
        
        // String extractedAnswer =
        //     "MARK SCHEME FOR CONTINUOUS WRITING\n\n" +

        //     "CONTENT (10 MARKS):\n" +
        //     "Mark Range 9–10:\n" +
        //     " - All aspects of the task are fully addressed and developed in detail.\n\n" +
        //     "Mark Range 7–8:\n" +
        //     " - All aspects of the task are addressed with some development.\n\n" +
        //     "Mark Range 5–6:\n" +
        //     " - Some aspects of the task are addressed with some development.\n\n" +
        //     "Mark Range 3–4:\n" +
        //     " - Some aspects of the task are addressed.\n\n" +
        //     "Mark Range 1–2:\n" +
        //     " - Some attempts to address the task.\n\n" +
        //     "Mark Range 0:\n" +
        //     " - No creditable response.\n\n" +

        //     "LANGUAGE (20 MARKS):\n" +
        //     "Mark Range 17–20:\n" +
        //     " - Coherent and cohesive presentation of ideas across the whole of the response.\n" +
        //     " - Effective use of ambitious vocabulary and grammar structures.\n" +
        //     " - Complex vocabulary, grammar, punctuation and spelling used accurately.\n\n" +
        //     "Mark Range 13–16:\n" +
        //     " - Coherent presentation of ideas with some cohesion between paragraphs.\n" +
        //     " - Vocabulary and grammar structures sufficiently varied to convey shades of meaning.\n" +
        //     " - Vocabulary, grammar, punctuation and spelling used mostly accurately.\n\n" +
        //     "Mark Range 9–12:\n" +
        //     " - Most ideas coherently presented with some cohesion within paragraphs.\n" +
        //     " - Vocabulary and grammar structures sufficiently varied to convey intended meaning.\n" +
        //     " - Vocabulary, grammar, punctuation and spelling often used accurately.\n\n" +
        //     "Mark Range 5–8:\n" +
        //     " - Some ideas coherently presented with attempts at achieving cohesion.\n" +
        //     " - Mostly simple vocabulary and grammar structures used; meaning is usually clear.\n" +
        //     " - Vocabulary, grammar, punctuation and spelling used with varying degrees of accuracy.\n\n" +
        //     "Mark Range 1–4:\n" +
        //     " - Ideas presented in isolation.\n" +
        //     " - Simple vocabulary and grammar structures used.\n" +
        //     " - A few examples of correct use of vocabulary, grammar, punctuation and spelling.\n\n" +
        //     "Mark Range 0:\n" +
        //     " - No creditable response.";

        //store extracted answer in GradingCriteria
        rubric.setGradingCriteria(extractedAnswer);

        // save and persist
        return markingRubricRepo.save(rubric);
    }

    @Override
    @Transactional
    public MarkingRubric extractQuestionsFromPNG(Long rubricId) throws IOException {
        // Retrieve the rubric by ID
        MarkingRubric rubric = markingRubricRepo.findById(rubricId)
        .orElseThrow(() -> new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found"));
        
        //get images from marking_rubric
        List<MarkingRubricImage> images = rubric.getQuestionImages();
        if (images.isEmpty()) {
            throw new IllegalArgumentException("No question images found for rubric ID " + rubricId);
        }


        // Convert each image's byte[] into a Media object
        List<Media> mediaList = images.stream()
            .map(img -> new Media(MimeTypeUtils.IMAGE_PNG, new ByteArrayResource(img.getImageData())))
            .collect(Collectors.toList());

        // Extract only the questions from the image
        String questionPrompt = """
            Extract only the essay question text and the associated images from the uploaded file. Do not include any introductory or explanatory text.
        """;

        var questionUserMessage = new UserMessage(questionPrompt, mediaList);
        var questionPromptObj = new Prompt(List.of(questionUserMessage));

        var questionResponseSpec = chatClient
            .prompt(questionPromptObj)  
            .options(OpenAiChatOptions.builder().build())
            .call();

        var questionChatResponse = questionResponseSpec.chatResponse();
        String extractedQuestions = questionChatResponse.getResult().getOutput().getText();

        // Store extracted questions into the questions
        rubric.setQuestions(extractedQuestions);

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

    @Override
    @Transactional
    public MarkingRubric addDocumentToRubric(Long rubricId, MultipartFile document) throws IOException {
        Optional<MarkingRubric> optionalRubric = markingRubricRepo.findById(rubricId);
        if (optionalRubric.isEmpty()) {
            throw new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found");
        }
        MarkingRubric rubric = optionalRubric.get();
        if (rubric.getImages() == null) {
            rubric.setImages(new ArrayList<>());
        }

        // Load the PDF document using PDFBox
        try (PDDocument pdfDoc = PDDocument.load(document.getInputStream())) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
            for (int page = 0; page < pdfDoc.getNumberOfPages(); page++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 100); // DPI (adjust as needed. Higher = slower but better detail)
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bim, "png", baos);
                byte[] pngData = baos.toByteArray();

                MarkingRubricImage rubricImage = new MarkingRubricImage();
                rubricImage.setImageData(pngData);
                rubricImage.setRubric(rubric);
                rubric.getImages().add(rubricImage);
            }
        }

        return markingRubricRepo.save(rubric);
    }

    @Override
    @Transactional
    public MarkingRubric addDocumentToQuestion(Long rubricId, MultipartFile document) throws IOException {
        Optional<MarkingRubric> optionalRubric = markingRubricRepo.findById(rubricId);
        if (optionalRubric.isEmpty()) {
            throw new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found");
        }
        MarkingRubric rubric = optionalRubric.get();
        if (rubric.getQuestionImages() == null) {
            rubric.setQuestionImages(new ArrayList<>());
        }

        // Load the PDF document using PDFBox
        try (PDDocument pdfDoc = PDDocument.load(document.getInputStream())) {
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
            for (int page = 0; page < pdfDoc.getNumberOfPages(); page++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 100);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bim, "png", baos);
                byte[] pngData = baos.toByteArray();

                MarkingRubricImage rubricImage = new MarkingRubricImage();
                rubricImage.setImageData(pngData);
                rubricImage.setRubric(rubric);
                rubric.getQuestionImages().add(rubricImage);
            }
        }

        return markingRubricRepo.save(rubric);
    }

    @Override
    @Transactional
    public MarkingRubric addStudentToRubric(Long rubricId, long studentId) {
        MarkingRubric rubric = markingRubricRepo.findById(rubricId)
            .orElseThrow(() -> new IllegalArgumentException("MarkingRubric with ID " + rubricId + " not found"));
        rubric.addStudentToRubric(studentId);
        return markingRubricRepo.save(rubric);
    }

    @Override
    public List<MarkingRubric> getRubricsByStudentAndClass(Long studentId, Long classroomId) {
        return markingRubricRepo.findByClassroomIdAndStudentId(classroomId, studentId);
    }

    @Override
    public List<MarkingRubric> getRubricsByClassroom(Long classroomId) {
        return markingRubricRepo.findByClassroomId(classroomId);
    }

}