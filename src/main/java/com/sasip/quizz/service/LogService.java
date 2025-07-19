package com.sasip.quizz.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sasip.quizz.model.LogEntry;

public interface LogService {

    // Log method that includes old and new values
    void log(String level, String source, String action, String actionDescription, String message, String performedBy, String previousValue, String newValue, String entity, String section);

    // Overloaded method for simple logging without previousValue and newValue
    void log(String level, String source, String action, String actionDescription, String message, String performedBy);

    // Filtering logs based on various criteria
    Page<LogEntry> filterLogs(
        String level,
        String source,
        String performedBy,
        LocalDateTime from,
        LocalDateTime to,
        Pageable pageable
    );
}
