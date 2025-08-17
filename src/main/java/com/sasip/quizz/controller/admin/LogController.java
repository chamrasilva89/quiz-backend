package com.sasip.quizz.controller.admin;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.model.LogEntry;
import com.sasip.quizz.service.LogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

@GetMapping 
public ResponseEntity<ApiResponse<?>> getLogs(
        @RequestParam(defaultValue = "") String level,
        @RequestParam(defaultValue = "") String source,
        @RequestParam(defaultValue = "") String performedBy,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    // Create a Pageable object with sorting by 'timestamp' in descending order
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("timestamp")));

    // Fetch the filtered logs
    Page<LogEntry> logPage = logService.filterLogs(level, source, performedBy, from, to, pageable);

    // Prepare the response map
    Map<String, Object> response = new HashMap<>();
    response.put("items", logPage.getContent());
    response.put("totalItems", logPage.getTotalElements());
    response.put("totalPages", logPage.getTotalPages());
    response.put("currentPage", logPage.getNumber());

    // Return the response
    return ResponseEntity.ok(new ApiResponse<>(response));
}

}
