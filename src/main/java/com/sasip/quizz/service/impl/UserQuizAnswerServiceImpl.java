package com.sasip.quizz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sasip.quizz.dto.QuizSubmissionRequest;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.UserQuizAnswer;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.repository.UserQuizAnswerRepository;
import com.sasip.quizz.service.UserQuizAnswerService;

@Service
public class UserQuizAnswerServiceImpl implements UserQuizAnswerService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserQuizAnswerRepository answerRepository;

    @Override
    public void submitQuizAnswers(QuizSubmissionRequest request) {
        for (QuizSubmissionRequest.AnswerSubmission answer : request.getAnswers()) {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            boolean isCorrect = question.getCorrectAnswerId().equals(answer.getSubmittedAnswerId());
            int points = isCorrect ? question.getPoints() : 0;

            UserQuizAnswer answerEntity = new UserQuizAnswer();
            answerEntity.setUserId(request.getUserId());
            answerEntity.setQuizId(request.getQuizId());
            answerEntity.setQuestionId(answer.getQuestionId());
            answerEntity.setSubmittedAnswerId(answer.getSubmittedAnswerId());
            answerEntity.setCorrectAnswerId(question.getCorrectAnswerId());
            answerEntity.setIsCorrect(isCorrect);
            answerEntity.setAwardedPoints(points);

            answerRepository.save(answerEntity);
        }
    }
}
