package com.sasip.quizz.service.impl;


import com.sasip.quizz.dto.QuestionWithoutAnswerDTO;
import com.sasip.quizz.dto.QuizRequest;
import com.sasip.quizz.dto.QuizResponse;
import com.sasip.quizz.dto.SasipQuizResponse;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizType;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuestionRepository questionRepository;
@Override
public Quiz createQuizFromRequest(QuizRequest request) {
    Quiz quiz = new Quiz();
    quiz.setQuizName(request.getQuizName());
    quiz.setIntro(request.getIntro());
    quiz.setModuleList(request.getModuleList());
    quiz.setRewardIdList(request.getRewardIdList());
    quiz.setAttemptsAllowed(request.getAttemptsAllowed());
    quiz.setPassAccuracy(request.getPassAccuracy());
    quiz.setTimeLimit(request.getTimeLimit());
    quiz.setXp(request.getXp());
    quiz.setScheduledTime(request.getScheduledTime());
    quiz.setDeadline(request.getDeadline());
    quiz.setAlYear(request.getAlYear());
    quiz.setQuestionIds(request.getQuestionIds());
    quiz.setQuizType(QuizType.valueOf(request.getQuizType().toUpperCase()));

    return quizRepository.save(quiz); // ID is auto-generated
}


    @Override
    public Optional<Quiz> getQuizById(Long id) { 
        return quizRepository.findById(id);
    }

    @Override
    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz updateQuizQuestions(Long quizId, List<Long> questionIds) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        if (!quizOptional.isPresent()) {
            throw new RuntimeException("Quiz not found");
        }

        Quiz quiz = quizOptional.get();
        quiz.setQuestionIds(questionIds);
        return quizRepository.save(quiz);
    }

    @Override
    public QuizResponse getQuizWithQuestions(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));

        List<Long> questionIds = quiz.getQuestionIds();
        List<Question> questions = questionRepository.findAllById(questionIds);

        return new QuizResponse(quiz, questions);
    }

    @Override
    public Page<QuizResponse> getAllQuizzesWithQuestions(Pageable pageable) {
        Page<Quiz> quizzes = quizRepository.findAll(pageable);
    
        List<QuizResponse> quizResponses = quizzes.stream()
            .map(quiz -> {
                List<Long> questionIds = quiz.getQuestionIds();
                List<Question> questions;
    
                // If questionIds is null or empty, set questions as an empty list
                if (questionIds == null || questionIds.isEmpty()) {
                    questions = Collections.emptyList();
                } else {
                    questions = questionRepository.findAllById(questionIds);
                }
    
                return new QuizResponse(quiz, questions);
            })
            .collect(Collectors.toList());
    
        return new PageImpl<>(quizResponses, pageable, quizzes.getTotalElements());
    }
    
    @Override
    public Page<SasipQuizResponse> getAllSasipQuizzesWithQuestions(Pageable pageable) {
        // Filter quizzes by SASIP type
        Page<Quiz> quizzes = quizRepository.findByQuizType(QuizType.SASIP, pageable);

        List<SasipQuizResponse> quizResponses = quizzes.stream()
            .map(quiz -> {
                List<Long> questionIds = quiz.getQuestionIds();
                List<Question> questions = (questionIds == null || questionIds.isEmpty())
                        ? Collections.emptyList()
                        : questionRepository.findAllById(questionIds);

                List<QuestionWithoutAnswerDTO> questionDTOs = questions.stream()
                        .map(QuestionWithoutAnswerDTO::new)
                        .collect(Collectors.toList());

                SasipQuizResponse response = new SasipQuizResponse(quiz, questionDTOs);
                response.setXp(quiz.getXp());
                response.setPassAccuracy(quiz.getPassAccuracy());
                try {
                    response.setAlYear(Integer.parseInt(quiz.getAlYear()));
                } catch (NumberFormatException e) {
                    response.setAlYear(0); // Default or handle gracefully
                }
                response.setAttemptsAllowed(quiz.getAttemptsAllowed());
                response.setScheduledTime(quiz.getScheduledTime());
                response.setDeadline(quiz.getDeadline());
                response.setRewardIds(quiz.getRewardIdList());

                return response;
            })
            .collect(Collectors.toList());

        return new PageImpl<>(quizResponses, pageable, quizzes.getTotalElements());
    }


}
