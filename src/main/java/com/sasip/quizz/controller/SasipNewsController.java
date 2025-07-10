package com.sasip.quizz.controller;

import com.sasip.quizz.dto.SasipNewsRequest;
import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.NewsListResponse;
import com.sasip.quizz.model.SasipNews;
import com.sasip.quizz.service.SasipNewsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/news")
public class SasipNewsController {

    @Autowired
    private SasipNewsService sasipNewsService;

    // Create a news post
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createNews(@RequestBody SasipNewsRequest request) {
        SasipNews news = sasipNewsService.createNews(request);
        // Returning single news item in the response
        Map<String, Object> data = new HashMap<>();
        data.put("items", List.of(news));
        return ResponseEntity.ok(new ApiResponse<>(data));
    }

    // Update a news post
    @PutMapping("/{newsId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateNews(@PathVariable Long newsId, @RequestBody SasipNewsRequest request) {
        SasipNews news = sasipNewsService.updateNews(newsId, request);
        // Returning updated news item in the response
        Map<String, Object> data = new HashMap<>();
        data.put("items", List.of(news));
        return ResponseEntity.ok(new ApiResponse<>(data));
    }

    // Delete a news post
@DeleteMapping("/{newsId}")
public ResponseEntity<ApiResponse<Map<String, Object>>> deleteNews(@PathVariable Long newsId) {
    try {
        sasipNewsService.deleteNews(newsId);

        // Prepare a custom response with status and message
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "News item deleted successfully.");

        return ResponseEntity.ok(new ApiResponse<>(response));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Error deleting news item.", 500));
    }
}


    // Get all news posts with pagination
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllNews(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NewsListResponse> newsPage = sasipNewsService.getAllNews(pageable);

        // Constructing response for paginated results
        Map<String, Object> response = new HashMap<>();
        response.put("items", newsPage.getContent());
        response.put("currentPage", newsPage.getNumber());
        response.put("totalItems", newsPage.getTotalElements());
        response.put("totalPages", newsPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    // Get a specific news post by ID
    @GetMapping("/{newsId}")
    public ResponseEntity<ApiResponse<?>> getNewsById(@PathVariable Long newsId) {
        try {
            SasipNews news = sasipNewsService.getNewsById(newsId);
            // Returning single news item in the response
            Map<String, Object> data = new HashMap<>();
            data.put("items", List.of(news)); // Ensure it's a list of one item
            return ResponseEntity.ok(new ApiResponse<>(data));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch news details", 500));
        }
    }
}
