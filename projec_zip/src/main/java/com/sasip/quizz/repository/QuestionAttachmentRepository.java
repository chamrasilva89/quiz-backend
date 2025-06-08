package com.sasip.quizz.repository;

import com.sasip.quizz.model.QuestionAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAttachmentRepository extends JpaRepository<QuestionAttachment, Long> {
}