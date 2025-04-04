package com.example.service.impl;

import com.example.service.*;
import com.example.model.*;
import com.example.repository.MarkingRubricRepo;
import com.example.repository.SubmissionRepo;

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

//Character matching imports
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;


@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepo submissionRepo;
    private final ChatClient chatClient;
    private final AnalysisService analysisService;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepo submissionRepo, ChatClient chatClient, AnalysisService analysisService) {
        this.submissionRepo = submissionRepo;
        this.chatClient = chatClient;
        this.analysisService = analysisService;
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
    public List<Submission> getSubmissionsByClassroomIdAndStudentId(Long classroomId, Long studentId) {
        return submissionRepo.findByClassroomIdAndStudentId(classroomId, studentId);
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
            Extract the chosen question number and the respective essay topic in this format:
            Chosen Question Number: ..
            Essay Topic: ..

            Then extract the student's written answer for the question 

            Students Essay: ..

            Extract only the content present in the image without additional commentary.
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
        
        // Retrieve the questions, grading criteria, and student's answer
        String questions = submission.getMarkingRubric().getQuestions();
        String gradingCriteria = submission.getMarkingRubric().getGradingCriteria();
        String writtenAnswer = submission.getWrittenAnswer();

        // Create a system message that contains the full model script.
        // This message provides the full benchmark reference for the grader.
        UserMessage systemMessage = new UserMessage(
            "MODEL SCRIPT Reference:\n" +
            "Question: ‘Social media does more harm than good.’ Do you agree? Why or why not\n" +
            "Model Script: Fashion model, Gigi Hadid once said, “We get to live in a time where we get to use social media as a tool.” Just like any other tool, be it a screwdriver, hammer, or wrench, we can use social media to put things together. Unfortunately, this also means that social media can be used to pull things apart. Social media has practically become a staple of modern society as institutions, organisations, and even governments rely on the outreach that social media provides. Yet, it is prudent to take a step back and ask if social media has been more of a boon or bane to our society. Despite the proliferation of misinformation, I believe that social media has brought more good than harm given that it allows people to stay connected with their loved ones and has helped boost businesses and economies.\n\n" +
            "Social media has been extremely instrumental in allowing people to connect with their loved ones all over the world. This is evident from a study published in Cyberpsychology, Behaviour, and Social Networking which found that social media use is associated with increased perceived social support and reduced feelings of loneliness among users, particularly in adolescents and young adults. This is entirely understandable as platforms like Facebook, Instagram, and WhatsApp allow people to connect with their friends and family that are not in the same geographical location. While early inventions like the telephone or even email allow the same type of connection, the advent of social media is pivotal as it not only allows for communication, but it lets people stay up to date on the daily ongoings of their loved ones. When one uploads a photograph to Facebook, for example, it can be viewed by a family member halfway across the globe. This, arguably, lets users gain a deeper sense of connection to their friends and family that are not physically around. This in turn enhances their emotional connection and brings a clear social benefit to the social media user. Thus, the social benefits of social media are definitively apparent.\n\n" +
            "However, some argue that the spread of misinformation due to social media leads to harmful and disastrous consequences for social media users. Indeed, research from the Massachusetts Institute of Technology found that false news stories on Twitter spread significantly faster and to a much larger audience than true stories. Falsehoods were 70% more likely to be retweeted than accurate news stories. The impact of the spread of misinformation can truly be troubling given the consequences they bring about in the real world. During the 2016 Presidential Election in the US, fake news about Hillary Clinton’s poor health led many to believe that she would not be fit to take office. This actually affected how people felt about her as a viable candidate for the US presidency. It becomes apparent that the spread of fake news through social media is a bane to society.\n\n" +
            "Yet, it must be said that government intervention directly addresses the issue of misinformation and social media greatly helps political parties garner support for just causes. Governments remain aware of the potential harms of fake news on social media and have taken steps to circumvent this issue. In the United Kingdom, research found that more than 83% of adults who had come across misinformation online in the past year would take initiative to verify information through trusted sources after being encouraged to do so by government bodies. The potential harms of social media can truly be overcome by government intervention. Furthermore, social media is crucial for the garnering of support by political parties. In Singapore, the People’s Action Party utilise their TikTok account to connect with younger generations and share important information. The reliance on social media platforms by governments across the globe demonstrates that social media has indeed brought huge benefits to society. Importantly, it also indicates that whatever harms social media brings can be adequately mitigated.\n\n" +
            "Furthermore, the financial benefits that social media has brought to businesses cannot be understated. These financial gains then truly help boost the progress of economies. A prime example of this is PeachyBB Slime that started on TikTok. The teenage girls who started the business used the platform to gain over 4 million followers. The business is worth over $40 million dollars today. Without the existence of the massive social media platform, such an achievement would not have been possible. The opportunities that social media awards aspiring entrepreneurs are unlike anything that has ever existed. This creation of opportunity inspires further business innovation that boosts economies. It is worth noting that large multinational organisations, like Nike and Apple, also use their social media presence for marketing purposes. With an abundance of followers, these businesses are able to market their products directly to the consumers. As social media enhances businesses, individual economies also reap the rewards as they grow bigger. The power of social media is one that can make tangible financial differences to the lives of many and this, in itself, is a benefit that must be noted and celebrated. Thus, it becomes apparent that social media has brought more benefits than drawbacks to society.\n\n" +
            "Ultimately, it is apparent that despite the unfortunate inevitability of misinformation being spread online, the social and economic benefits of social media outweigh the drawbacks. Social media has truly revolutionised the way that we communicate with one another in modern society. The reliance on the various social media platforms for diverse aspects of life demonstrates its significance, and more significantly, illustrates the importance of social media as a tool that can be used to bring people and communities together, or to tear them apart. It is most definitely true that social media is a tool that we get to use. The responsibility, then, is on all of us to use it for the right reasons.",
            Collections.emptyList()
        );

        // Create the user message with grading instructions and the student's submission
        UserMessage userMessage = new UserMessage(
            String.format(
                "Grade the following student's essay strictly using the grading criteria below. " +
                "Critically assess both content and language, ensuring the consistent use of the PEEL structure (Point, Evidence, Explanation, Link) " +
                "in the student's arguments. Deduct marks for underdeveloped points, unsupported claims, overly simplistic language, and deviations from the expected structure. " +
                "Compare the student's work against the model script provided in the system message. " +
                "Grading Criteria:\n%s\n\n" +
                "Student's Submission:\n%s\n\n" +
                "Provide detailed, critical feedback on the student's submission, highlighting both strengths and weaknesses." +
                "Provide the individual marks for each component in the grading criteria " +
                "and conclude with a final score on a new line prefixed with 'Total Score:' (e.g., 'Score: 18/30')."  + 
                "The total score should be out of 30 since the first component is worth 10 marks and the second component is worth 20 marks.",
                gradingCriteria, writtenAnswer
            )
        );

        // Build the prompt using both messages
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        
        // Call the OpenAI API to get the feedback
        var responseSpec = chatClient.prompt(prompt)
            .options(OpenAiChatOptions.builder().model("gpt-4").build())
            .call();
        String feedback = responseSpec.chatResponse().getResult().getOutput().getText();
        // add feedback to Analysis object
        List<FeedbackHistory> allFeedbacks = analysisService.addFeedbackToAnalysis(analysisId, feedback);
        // update Summary for latest feedback
        analysisService.createAnalysisSummary(analysisId, allFeedbacks);

        // Extract the score from the feedback using regex
        Pattern pattern = Pattern.compile("Score:\\s*(\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(feedback);
        if (matcher.find()) {
            String scoreStr = matcher.group(1);
            try {
                Double scoreOutOf30 = Double.parseDouble(scoreStr);
                Double scoreOutOf100 = (scoreOutOf30 / 30.0) * 100.0;
                submission.setScore(scoreOutOf100);
            } catch (NumberFormatException e) {
                // Optionally log the error or set a default score
            }
        }

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
