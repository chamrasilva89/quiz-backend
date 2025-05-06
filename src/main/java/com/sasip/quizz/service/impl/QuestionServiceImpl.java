package com.sasip.quizz.service.impl;

import org.springframework.stereotype.Service;
import com.sasip.quizz.dto.QuestionRequest;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.repository.QuestionRepository;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question addQuestion(QuestionRequest request) {
        Question question = new Question();
        question.setQuizId(request.getQuizId());
        question.setQuestionText(request.getQuestionText());
        question.setOptions(request.getOptions());
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
        return questionRepository.findById(questionId).orElse(null);
    }
}
