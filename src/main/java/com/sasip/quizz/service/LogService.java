package com.sasip.quizz.service;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sasip.quizz.model.LogEntry;

public interface LogService {
    void log(String level, String source, String action, String message, String performedBy);
    void log(String level, String source, String action, String message, String performedBy, String previousValue, String newValue, String entity, String section);

    Page<LogEntry> filterLogs(
        String level,
        String source,
        String performedBy,
        LocalDateTime from,
        LocalDateTime to,
        Pageable pageable
    );
}
