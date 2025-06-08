package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ApiResponse;
import com.sasip.quizz.dto.QuestionFilterRequest;
import com.sasip.quizz.dto.QuestionPatchRequest;
import com.sasip.quizz.dto.QuestionRequest;
import com.sasip.quizz.exception.ResourceNotFoundException;
import com.sasip.quizz.model.Question;
import com.sasip.quizz.service.QuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "Question Controller", description = "Handles single question related operations")
@CrossOrigin
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    

    @Operation(summary = "Add single question", description = "Add single question")
    @PostMapping("/add")
    public ResponseEntity<?> addQuestion(@Valid @RequestBody QuestionRequest request) {
        try {
            Question question = questionService.addQuestion(request);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(question)); // wrap single item inside list
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(e.getMessage(), 500));
        }
    }


    @Operation(summary = "Get all questions (paginated)", description = "Get all questions with pagination support")
    @GetMapping("/all")
    public ResponseEntity<?> getAllQuestions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<Question> questionPage = questionService.getAllQuestions(PageRequest.of(page, size));
            Map<String, Object> response = new HashMap<>();
            response.put("items", questionPage.getContent()); // unified key
            response.put("currentPage", questionPage.getNumber());
            response.put("totalItems", questionPage.getTotalElements());
            response.put("totalPages", questionPage.getTotalPages());

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch questions", 500));
        }
    }


    
    @Operation(summary = "Get single question", description = "Get single question by question ID")
    @GetMapping("/{questionId}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long questionId) {
        try {
            Question question = questionService.getQuestionById(questionId);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(question)); // wrap single question in list
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Unexpected error", 500));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateQuestionPartial(
            @PathVariable Long id,
            @RequestBody QuestionPatchRequest updates) {
        try {
            Question updated = questionService.updateQuestionPartial(id, updates);
            Map<String, Object> response = new HashMap<>();
            response.put("items", List.of(updated));  // wrap updated object in list
            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ex.getMessage(), 404));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update question", 500));
        }
    }

    @Operation(summary = "Filter questions with pagination", description = "Filters questions by modules, submodules and difficulty levels")
    @RequestMapping(value = "/filter", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<Map<String, Object>>> filterQuestions(
            @RequestBody QuestionFilterRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Question> questionPage = questionService.getFilteredQuestions(request, PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("items", questionPage.getContent());
        response.put("currentPage", questionPage.getNumber());
        response.put("totalItems", questionPage.getTotalElements());
        response.put("totalPages", questionPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponse<>(response));
    }

}