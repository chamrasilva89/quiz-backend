package com.sasip.quizz.service.impl;


import com.sasip.quizz.dto.QuizRequest;
import com.sasip.quizz.dto.QuizResponse;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    if (quiz.getQuizId() == null || quiz.getQuizId().isEmpty()) {
        quiz.setQuizId(UUID.randomUUID().toString());
    }
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
    quiz.setQuestionIds(request.getQuestionIds()); // just IDs, no cascade problems

    return quizRepository.save(quiz);
}


    @Override
    public Optional<Quiz> getQuizById(String id) { 
        return quizRepository.findById(id);
    }

    @Override
    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }

        @Override
    public Quiz updateQuizQuestions(String quizId, List<Long> questionIds) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);
        if (!quizOptional.isPresent()) {
            throw new RuntimeException("Quiz not found");
        }

        Quiz quiz = quizOptional.get();
        quiz.setQuestionIds(questionIds);  // Set new question IDs
        return quizRepository.save(quiz);  // Save the updated quiz
    }



    @Override
    public QuizResponse getQuizWithQuestions(String quizId) {
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
                List<Question> questions = questionRepository.findAllById(questionIds);
                return new QuizResponse(quiz, questions);
            })
            .collect(Collectors.toList());
        return new PageImpl<>(quizResponses, pageable, quizzes.getTotalElements());
    }

}
