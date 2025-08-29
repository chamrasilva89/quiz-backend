package com.sasip.quizz.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sasip.quizz.dto.QuestionFilterRequest;
import com.sasip.quizz.dto.QuestionPatchRequest;
import com.sasip.quizz.dto.QuestionRequest;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.DifficultyLevel;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.QuestionAttachment;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.service.QuestionService;
import com.sasip.quizz.util.DifficultyPointMapping;
import com.sasip.quizz.service.LogService;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final LogService logService;

    public QuestionServiceImpl(QuestionRepository questionRepository, LogService logService) {
        this.questionRepository = questionRepository;
        this.logService = logService;
    }

    @Override
    public Question addQuestion(QuestionRequest request) {
        Question question = new Question();
        question.setQuestionText(request.getQuestionText());
        question.setOptions(request.getOptions());
        question.setCorrectAnswerId(request.getCorrectAnswerId());
        question.setExplanation(request.getExplanation());
        question.setSubject(request.getSubject());
        question.setType(request.getType());
        question.setSubType(request.getSubType());
        question.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getPoints() != null) {
            question.setPoints(request.getPoints());
        } else {
            try {
                DifficultyLevel level = DifficultyLevel.valueOf(request.getDifficultyLevel().toUpperCase());
                int autoPoints = DifficultyPointMapping.getPointsForDifficulty(level);
                question.setPoints(autoPoints);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid difficulty level: " + request.getDifficultyLevel());
            }
        }


        question.setMaxTimeSec(request.getMaxTimeSec());
        question.setAlYear(request.getAlYear());
        question.setHasAttachment(request.isHasAttachment());
        question.setModule(request.getModule());
        question.setSubmodule(request.getSubmodule());

        if (request.isHasAttachment()) {
            List<QuestionAttachment> attachments = new ArrayList<>();
            for (String path : request.getAttachmentPaths()) {
                QuestionAttachment attachment = new QuestionAttachment();
                attachment.setFilePath(path);
                attachment.setQuestion(question);
                attachments.add(attachment);
            }
            question.setAttachments(attachments);
        }

        Question saved = questionRepository.save(question);
        //logService.log("INFO", "QuestionServiceImpl", "Add Question", "Created question ID: " + saved.getQuestionId(), "system");
        return saved;
    }

    @Override
    public Page<Question> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + questionId));
    }

@Override
public Question updateQuestionPartial(Long id, QuestionPatchRequest request) {
    // 1. Fetch the existing question from the database
    Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + id));

    // 2. Conditionally update each field only if a new value is provided
    if (request.getQuestionText() != null && !request.getQuestionText().isBlank()) {
        question.setQuestionText(request.getQuestionText());
    }
    if (request.getExplanation() != null) {
        question.setExplanation(request.getExplanation());
    }
    if (request.getStatus() != null && !request.getStatus().isBlank()) {
        question.setStatus(request.getStatus());
    }
    if (request.getCorrectAnswerId() != null) {
        question.setCorrectAnswerId(request.getCorrectAnswerId());
    }
    if (request.getAlYear() != null) {
        question.setAlYear(request.getAlYear());
    }
    if (request.getOptions() != null && !request.getOptions().isEmpty()) {
        question.setOptions(request.getOptions());
    }
    if (request.getSubject() != null) {
        question.setSubject(request.getSubject());
    }
    if (request.getType() != null) {
        question.setType(request.getType());
    }
    if (request.getSubType() != null) {
        question.setSubType(request.getSubType());
    }
    if (request.getPoints() != null) {
        question.setPoints(request.getPoints());
    }
    if (request.getDifficultyLevel() != null) {
        question.setDifficultyLevel(request.getDifficultyLevel());
    }
    if (request.getMaxTimeSec() != null) {
        question.setMaxTimeSec(request.getMaxTimeSec());
    }
    if (request.getHasAttachment() != null) {
        question.setHasAttachment(request.getHasAttachment());
    }
    if (request.getModule() != null) {
        question.setModule(request.getModule());
    }
    if (request.getSubmodule() != null) {
        question.setSubmodule(request.getSubmodule());
    }

    // 3. Save the updated question
    return questionRepository.save(question);
}

    @Override
    public Page<Question> getFilteredQuestions(QuestionFilterRequest request, Pageable pageable) {
        return questionRepository.findFilteredQuestions(
                request.getModules(), request.getSubmodules(), request.getDifficultyLevels(), pageable);
    }

}
