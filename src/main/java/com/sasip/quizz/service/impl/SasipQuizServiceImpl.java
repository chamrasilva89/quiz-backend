package com.sasip.quizz.service.impl;

// com.sasip.quizz.service.impl.SasipQuizServiceImpl.java


import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.QuizType;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.repository.UserQuizSubmissionRepository;
import com.sasip.quizz.service.SasipQuizService;
import com.sasip.quizz.spec.QuizSpecifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SasipQuizServiceImpl implements SasipQuizService {

    @Autowired private QuizRepository quizRepository;
    @Autowired private UserQuizSubmissionRepository userQuizSubmissionRepository;
@Override
public Page<SasipQuizSummary> findFiltered(SasipQuizFilterRequest filter) {
    // Build dynamic spec using filter object
    Specification<Quiz> spec = Specification
        .where(QuizSpecifications.isSasip())
        .and(QuizSpecifications.hasStatus(filter.getStatus()))
        .and(QuizSpecifications.hasAnyModule(filter.getModule()))
        .and(QuizSpecifications.timeLimitBetween(filter.getMinTimeLimit(), filter.getMaxTimeLimit()));

    Pageable pg = PageRequest.of(filter.getPage(), filter.getSize());

    Page<Quiz> quizPage = quizRepository.findAll(spec, pg);

    // Map to DTO
    return quizPage.map(SasipQuizSummary::new);
}

    public Page<SasipQuizListItem> listSasipQuizzesWithCompletion(
            Long userId, Pageable pageable, String alYear, QuizStatus status) {

        Specification<Quiz> spec = Specification
            .where((root, query, cb) -> cb.equal(root.get("quizType"), QuizType.SASIP));

        if (alYear != null && !alYear.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("alYear"), alYear));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("quizStatus"), status));
        }

        Page<Quiz> quizzes = quizRepository.findAll(spec, pageable);

        List<SasipQuizListItem> quizDTOs = quizzes.getContent().stream().map(quiz -> {
            boolean completed = userQuizSubmissionRepository
                .findByUserIdAndQuizId(userId, String.valueOf(quiz.getQuizId()))
                .isPresent();

            return new SasipQuizListItem(
                quiz.getQuizId(),
                quiz.getQuizName(),
                quiz.getIntro(),
                quiz.getXp(),
                quiz.getPassAccuracy(),
                quiz.getTimeLimit(),
                quiz.getAlYear(),
                quiz.getAttemptsAllowed(),
                quiz.getQuizStatus(),
                quiz.getScheduledTime(),
                quiz.getDeadline(),
                completed
            );
        }).toList();

        return new PageImpl<>(quizDTOs, pageable, quizzes.getTotalElements());
    }

    public SasipQuizStatsDTO getUserSasipStats(Long userId) {
        double best = Optional.ofNullable(userQuizSubmissionRepository.findMaxSasipScore(userId)).orElse(0.0);
        double avg = Optional.ofNullable(userQuizSubmissionRepository.findAvgSasipScore(userId)).orElse(0.0);
        long completed = userQuizSubmissionRepository.countCompletedSasipQuizzes(userId);
        long total = quizRepository.countAllSasipQuizzes();

        return new SasipQuizStatsDTO(best, avg, completed, total);
    }

}
