package com.sasip.quizz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sasip.quizz.model.LogEntry;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntry, Long> {
    Page<LogEntry> findByLevelContainingAndSourceContainingAndPerformedByContaining(
        String level, String source, String performedBy, Pageable pageable);
    Page<LogEntry> findAll(Specification<LogEntry> spec, Pageable pageable);

}
