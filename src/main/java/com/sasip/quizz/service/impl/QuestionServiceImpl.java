package com.sasip.quizz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasip.quizz.dto.QuestionRequest;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.service.QuestionService;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

@Autowired
private ObjectMapper objectMapper;

@Override
public Question addQuestion(QuestionRequest request) {
    Question question = new Question();

    question.setQuizId(request.getQuizId());
    question.setQuestionText(request.getQuestionText());

    // Convert List<String> to JSON String
    try {
        String optionsJson = objectMapper.writeValueAsString(request.getOptions());
        question.setOptions(optionsJson);
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Invalid options format", e);
    }

    // Map rest fields
    question.setCorrectOptionIndex(request.getCorrectOptionIndex());
    question.setExplanation(request.getExplanation());
    question.setSubject(request.getSubject());
    question.setType(request.getType());
    question.setSubType(request.getSubType());
    question.setPoints(request.getPoints());
    question.setDifficultyLevel(request.getDifficultyLevel());
    question.setMaxTimeSec(request.getMaxTimeSec());

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
