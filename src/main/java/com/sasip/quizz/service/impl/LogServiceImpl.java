package com.sasip.quizz.service.impl;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sasip.quizz.model.LogEntry;
import com.sasip.quizz.repository.LogRepository;
import com.sasip.quizz.service.LogService;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void log(String level, String source, String action, String message, String performedBy) {
        LogEntry entry = LogEntry.builder()
            .level(level)
            .source(source)
            .action(action)
            .message(message)
            .performedBy(performedBy)
            .timestamp(LocalDateTime.now())
            .build();
        logRepository.save(entry);
    }

    @Override
    public Page<LogEntry> filterLogs(String level, String source, String performedBy, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Specification<LogEntry> spec = Specification.where(null);

        if (level != null && !level.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("level")), "%" + level.toLowerCase() + "%"));
        }

        if (source != null && !source.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("source")), "%" + source.toLowerCase() + "%"));
        }

        if (performedBy != null && !performedBy.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("performedBy")), "%" + performedBy.toLowerCase() + "%"));
        }

        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from));
        }

        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to));
        }

        return logRepository.findAll(spec, pageable);
    }

}
