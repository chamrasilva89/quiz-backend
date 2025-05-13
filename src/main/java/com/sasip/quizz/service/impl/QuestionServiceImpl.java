package com.sasip.quizz.service.impl;

import org.springframework.stereotype.Service;

import com.sasip.quizz.dto.QuestionRequest;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.model.QuestionAttachment;
import com.sasip.quizz.model.Quiz;
import com.sasip.quizz.repository.QuestionRepository;
import com.sasip.quizz.service.QuestionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    question.setOptionsList(request.getOptions());
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
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + questionId));
    }


}
