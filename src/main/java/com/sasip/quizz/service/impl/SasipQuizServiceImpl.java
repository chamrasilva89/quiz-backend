package com.sasip.quizz.service.impl;

// com.sasip.quizz.service.impl.SasipQuizServiceImpl.java


import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.repository.QuizRepository;
import com.sasip.quizz.service.SasipQuizService;
import com.sasip.quizz.spec.QuizSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SasipQuizServiceImpl implements SasipQuizService {

    @Autowired private QuizRepository quizRepository;

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

}
