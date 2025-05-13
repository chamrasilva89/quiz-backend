package com.sasip.quizz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasip.quizz.dto.QuestionRequest;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.QuestionAttachment;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.service.QuestionService;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

@Autowired
private ObjectMapper objectMapper;

/* 
@Override
public Question addQuestion(QuestionRequest request) {
    Question question = new Question();

    question.setQuestionText(request.getQuestionText());

    // Convert List<String> to JSON String
    try {
        String optionsJson = objectMapper.writeValueAsString(request.getOptions());
        question.setOptions(optionsJson);
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Invalid options format", e);
    }

    question.setCorrectAnswerId(request.getCorrectAnswerId());
    question.setHasAttachment(request.isHasAttachment());
    question.setModule(request.getModule());
    question.setSubmodule(request.getSubmodule());

    // Map rest fields
    question.setExplanation(request.getExplanation());
    question.setSubject(request.getSubject());
    question.setType(request.getType());
    question.setSubType(request.getSubType());
    question.setPoints(request.getPoints());
    question.setDifficultyLevel(request.getDifficultyLevel());
    question.setMaxTimeSec(request.getMaxTimeSec());

    return questionRepository.save(question);
}*/
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
    question.setPoints(request.getPoints());
    question.setDifficultyLevel(request.getDifficultyLevel());
    question.setMaxTimeSec(request.getMaxTimeSec());
    question.setAlYear(request.getAlYear());
    question.setHasAttachment(request.isHasAttachment());
    question.setModule(request.getModule());
    question.setSubmodule(request.getSubmodule());
    // 🟡 Attachment handling (put this here)
    if (request.isHasAttachment()) {
        List<QuestionAttachment> attachments = new ArrayList<>();
        for (String path : request.getAttachmentPaths()) {
            QuestionAttachment attachment = new QuestionAttachment();
            attachment.setFilePath(path);
            attachment.setQuestion(question); // Set relationship
            attachments.add(attachment);
        }
        question.setAttachments(attachments); // Attach to question
    }

    return questionRepository.save(question);
}

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + questionId));
    }
}
