package com.sasip.quizz.service.impl;


import com.sasip.quizz.dto.QuizRequest;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.service.QuizService;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;

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
}
