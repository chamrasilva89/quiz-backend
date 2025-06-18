package com.sasip.quizz.service.impl;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.model.QuizStatus;
import com.sasip.quizz.model.QuizType;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.repository.UserQuizSubmissionRepository;
import com.sasip.quizz.service.SasipQuizService;
import com.sasip.quizz.spec.QuizSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SasipQuizServiceImpl implements SasipQuizService {

    @Autowired private QuizRepository quizRepository;
    @Autowired private UserQuizSubmissionRepository userQuizSubmissionRepository;
    @Autowired private QuestionRepository questionRepository;
    @Override
    public Page<SasipQuizSummary> findFiltered(SasipQuizFilterRequest filter) {
        Specification<Quiz> spec = Specification
            .where(QuizSpecifications.isSasip())
            .and(QuizSpecifications.hasStatus(filter.getStatus()))
            .and(QuizSpecifications.hasAnyModule(filter.getModule()))
            .and(QuizSpecifications.timeLimitBetween(filter.getMinTimeLimit(), filter.getMaxTimeLimit()));

        Pageable pg = PageRequest.of(filter.getPage(), filter.getSize());
        Page<Quiz> quizPage = quizRepository.findAll(spec, pg);

        return quizPage.map(SasipQuizSummary::new);
    }

    @Override
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
                completed,
                quiz.getModuleList(),
                quiz.getRewardIdList(),
                quiz.getQuestionIds() != null ? quiz.getQuestionIds().size() : 0
            );
        }).toList();

        return new PageImpl<>(quizDTOs, pageable, quizzes.getTotalElements());
    }


    @Override
    public SasipQuizStatsDTO getUserSasipStats(Long userId) {
        double best = Optional.ofNullable(userQuizSubmissionRepository.findMaxSasipScore(userId)).orElse(0.0);
        double avg = Optional.ofNullable(userQuizSubmissionRepository.findAvgSasipScore(userId)).orElse(0.0);
        long completed = userQuizSubmissionRepository.countCompletedSasipQuizzes(userId);
        long total = quizRepository.countAllSasipQuizzes();

        return new SasipQuizStatsDTO(best, avg, completed, total);
    }

    @Override
    public Page<SasipQuizListItem> listCompletedQuizzesOnly(Long userId, Pageable pageable, String alYear, QuizStatus status) {
        Specification<Quiz> spec = Specification
            .where((root, query, cb) -> cb.equal(root.get("quizType"), QuizType.SASIP));

        if (alYear != null && !alYear.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("alYear"), alYear));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("quizStatus"), status));
        }

        Page<Quiz> quizzes = quizRepository.findAll(spec, Pageable.unpaged());

        List<SasipQuizListItem> completedQuizzes = quizzes.getContent().stream()
            .filter(quiz -> userQuizSubmissionRepository
                .findByUserIdAndQuizId(userId, String.valueOf(quiz.getQuizId()))
                .isPresent())
            .map(quiz -> new SasipQuizListItem(
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
                true,
                quiz.getModuleList(),
                quiz.getRewardIdList(),
                quiz.getQuestionIds() != null ? quiz.getQuestionIds().size() : 0
            )).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), completedQuizzes.size());
        List<SasipQuizListItem> paginated = completedQuizzes.subList(start, end);

        return new PageImpl<>(paginated, pageable, completedQuizzes.size());
    }

    @Override
    public Page<QuizWithQuestionsDTO> filterQuizzesWithQuestions(QuizFilterRequest filter) {
        Specification<Quiz> spec = Specification
            .where(QuizSpecifications.hasStatus(filter.getStatus()))
            .and(QuizSpecifications.hasAnyModule(filter.getModules()))
            .and(QuizSpecifications.hasAlYear(filter.getAlYear()))
            .and(QuizSpecifications.isSasip());

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<Quiz> quizPage = quizRepository.findAll(spec, pageable);

        List<QuizWithQuestionsDTO> dtoList = quizPage.getContent().stream().map(quiz -> {
            QuizWithQuestionsDTO dto = new QuizWithQuestionsDTO();
            dto.setQuizId(quiz.getQuizId());
            dto.setQuizName(quiz.getQuizName());
            dto.setIntro(quiz.getIntro());
            dto.setQuizStatus(quiz.getQuizStatus());
            dto.setXp(quiz.getXp());
            dto.setTimeLimit(quiz.getTimeLimit());
            dto.setAlYear(quiz.getAlYear());
            dto.setModuleList(quiz.getModuleList());
            dto.setScheduledTime(quiz.getScheduledTime());
            dto.setDeadline(quiz.getDeadline());

            List<Long> questionIds = quiz.getQuestionIds();
            List<QuestionDTO> questions = questionRepository.findAllById(questionIds).stream().map(q -> {
                QuestionDTO qDto = new QuestionDTO();
                qDto.setQuestionId(q.getQuestionId());
                qDto.setAlYear(q.getAlYear());
                qDto.setQuestionText(q.getQuestionText());
                qDto.setOptions(q.getOptions());
                qDto.setStatus(q.getStatus());
                qDto.setCorrectAnswerId(q.getCorrectAnswerId());
                qDto.setExplanation(q.getExplanation());
                qDto.setSubject(q.getSubject());
                qDto.setType(q.getType());
                qDto.setSubType(q.getSubType());
                qDto.setPoints(q.getPoints());
                qDto.setDifficultyLevel(q.getDifficultyLevel());
                qDto.setMaxTimeSec(q.getMaxTimeSec());
                qDto.setHasAttachment(q.isHasAttachment());
                qDto.setModule(q.getModule());
                qDto.setSubmodule(q.getSubmodule());
                return qDto;
            }).toList();

            dto.setQuestions(questions);
            dto.setTotalQuestions(questions.size());

            return dto;
        }).toList();

        return new PageImpl<>(dtoList, pageable, quizPage.getTotalElements());
    }



} 
