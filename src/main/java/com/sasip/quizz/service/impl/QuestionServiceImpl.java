package com.sasip.quizz.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sasip.quizz.dto.QuestionFilterRequest;
import com.sasip.quizz.dto.QuestionPatchRequest;
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
    public Page<Question> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + questionId));
    }

    @Override
    public Question updateQuestionPartial(Long id, QuestionPatchRequest request) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Not found"));

        if (request.getQuestionText() != null)
            question.setQuestionText(request.getQuestionText());

        if (request.getExplanation() != null)
            question.setExplanation(request.getExplanation());

        if (request.getStatus() != null)
            question.setStatus(request.getStatus());

        if (request.getCorrectAnswerId() != null)
            question.setCorrectAnswerId(request.getCorrectAnswerId());

        return questionRepository.save(question);
    }

    @Override
    public Page<Question> getFilteredQuestions(QuestionFilterRequest request, Pageable pageable) {
        return questionRepository.findFilteredQuestions(
                request.getModules(), request.getSubmodules(), request.getDifficultyLevels(), pageable);
    }


}
