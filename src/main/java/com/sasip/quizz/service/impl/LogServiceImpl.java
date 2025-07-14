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
    private static final int MAX_TEXT_LENGTH = 60000;
    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void log(String level, String source, String action, String message, String performedBy) {
        log(level, source, action, message, performedBy, null, null, null, null);
    }

    @Override
public void log(String level, String source, String action, String message, String performedBy,
                String previousValue, String newValue, String entity, String section) {

    if (previousValue != null && previousValue.length() > MAX_TEXT_LENGTH) {
        previousValue = previousValue.substring(0, MAX_TEXT_LENGTH) + "...[TRUNCATED]";
    }

    if (newValue != null && newValue.length() > MAX_TEXT_LENGTH) {
        newValue = newValue.substring(0, MAX_TEXT_LENGTH) + "...[TRUNCATED]";
    }

    if (message != null && message.length() > MAX_TEXT_LENGTH) {
        message = message.substring(0, MAX_TEXT_LENGTH) + "...[TRUNCATED]";
    }

    LogEntry entry = LogEntry.builder()
        .level(level)
        .source(source)
        .action(action)
        .message(message)
        .performedBy(performedBy)
        .timestamp(LocalDateTime.now())
        .previousValue(previousValue)
        .newValue(newValue)
        .entity(entity)
        .section(section)
        .build();

    try {
        logRepository.save(entry);
    } catch (Exception e) {
        System.err.println("⚠️ Failed to log entry: " + e.getMessage());
        // Optionally log to file or fallback system
    }
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
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("timestamp"), from));
        }

        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("timestamp"), to));
        }

        return logRepository.findAll(spec, pageable);
    }
}
