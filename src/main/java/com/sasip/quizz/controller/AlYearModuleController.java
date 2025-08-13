package com.sasip.quizz.controller;

import com.sasip.quizz.dto.AlYearModuleRequestDTO;
import com.sasip.quizz.dto.AlYearModuleResponseDTO;
import com.sasip.quizz.service.AlYearModuleService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alyearmodules")
public class AlYearModuleController {

    private final AlYearModuleService alYearModuleService;

    public AlYearModuleController(AlYearModuleService alYearModuleService) {
        this.alYearModuleService = alYearModuleService;
    }

    // Create a new completed module
    @PostMapping
    public ResponseEntity<?> createAlYearModule(@RequestBody AlYearModuleRequestDTO requestDTO) {
        AlYearModuleResponseDTO response = alYearModuleService.createAlYearModule(requestDTO);
        return ResponseEntity.status(201).body(new ResponseWrapper<>(new ItemsWrapper<>(response)));
    }

    // Update a completed module
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateAlYearModule(@PathVariable Long id, @RequestBody AlYearModuleRequestDTO requestDTO) {
        AlYearModuleResponseDTO response = alYearModuleService.updateAlYearModule(id, requestDTO);
        return ResponseEntity.ok(new ResponseWrapper<>(new ItemsWrapper<>(response)));
    }

    // Get all completed modules for a specific AL Year
    @GetMapping("/{alYearId}")
    public ResponseEntity<?> getAllCompletedModulesForAlYear(@PathVariable Long alYearId) {
        List<AlYearModuleResponseDTO> response = alYearModuleService.getAllCompletedModulesForAlYear(alYearId);
        return ResponseEntity.ok(new ResponseWrapper<>(new ItemsWrapper<>(response)));
    }

    // ResponseWrapper class used for wrapping the response data in the required format (with data)
    public static class ResponseWrapper<T> {
        private T data;

        public ResponseWrapper(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    // ItemsWrapper class used for wrapping the response data in 'items' array
    public static class ItemsWrapper<T> {
        private List<T> items;

        public ItemsWrapper(T item) {
            // Create a list with the single item and wrap it in items[]
            this.items = List.of(item);
        }

        public ItemsWrapper(List<T> items) {
            this.items = items;
        }

        public List<T> getItems() {
            return items;
        }

        public void setItems(List<T> items) {
            this.items = items;
        }
    }
}
