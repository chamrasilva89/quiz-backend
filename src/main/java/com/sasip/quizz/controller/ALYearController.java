package com.sasip.quizz.controller;

import com.sasip.quizz.dto.ALYearRequestDTO;
import com.sasip.quizz.dto.ALYearResponseDTO;
import com.sasip.quizz.dto.PaginatedALYearResponseDTO;
import com.sasip.quizz.service.ALYearService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alyears")
public class ALYearController {

    private final ALYearService alYearService;

    public ALYearController(ALYearService alYearService) {
        this.alYearService = alYearService;
    }

    /**
     * Get all AL Years (Paginated)
     *
     * @param page the page number
     * @param size the page size
     * @return a paginated response containing totalItems, totalPages, currentPage, and items[]
     */
    @GetMapping
    public ResponseEntity<?> getAllALYears(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {

        // Fetch paginated response from service
        PaginatedALYearResponseDTO response = alYearService.getAllALYears(page, size);

        // Wrap response in 'data' and 'items' format
        return ResponseEntity.ok(new ResponseWrapper<>(response));
    }

    /**
     * Create a new AL Year
     *
     * @param requestDTO the AL Year data to create
     * @return the created AL Year wrapped in 'data'
     */
    @PostMapping
    public ResponseEntity<?> createALYear(@RequestBody ALYearRequestDTO requestDTO) {
        ALYearResponseDTO response = alYearService.createALYear(requestDTO);

        // Wrap the response in 'data' -> 'items' format
        return ResponseEntity.status(201).body(new ResponseWrapper<>(new ItemsWrapper<>(response)));
    }

    /**
     * Update an existing AL Year
     *
     * @param id         the ID of the AL Year to update
     * @param requestDTO the updated AL Year data
     * @return the updated AL Year wrapped in 'data'
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateALYear(@PathVariable Long id, @RequestBody ALYearRequestDTO requestDTO) {
        ALYearResponseDTO response = alYearService.updateALYear(id, requestDTO);
        
        // Wrap the updated ALYearResponseDTO in 'data' -> 'items' format
        return ResponseEntity.ok(new ResponseWrapper<>(new ItemsWrapper<>(response)));
    }


    /**
     * Delete an AL Year by ID
     *
     * @param id the ID of the AL Year to delete
     * @return a response indicating success (no content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteALYear(@PathVariable Long id) {
        alYearService.deleteALYear(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get an AL Year by its ID
     *
     * @param id the ID of the AL Year
     * @return the AL Year wrapped in 'data'
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getALYear(@PathVariable Long id) {
        ALYearResponseDTO response = alYearService.getALYear(id);
        return ResponseEntity.ok(new ResponseWrapper<>(response));
    }

    /**
     * Mark an AL Year as the current year
     *
     * @param id the ID of the AL Year to mark as current
     * @return a response indicating success (no content)
     */
    @PostMapping("/{id}/current")
    public ResponseEntity<Void> markCurrentYear(@PathVariable Long id) {
        alYearService.markCurrentYear(id);
        return ResponseEntity.noContent().build();
    }

    // Wrapper class to return responses in the required format (with data)
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

    public static class ItemsWrapper<T> {
        private List<T> items;

        public ItemsWrapper(T item) {
            // Create a list with the single item and wrap it in items[]
            this.items = List.of(item);
        }

        public List<T> getItems() {
            return items;
        }

        public void setItems(List<T> items) {
            this.items = items;
        }
    }
}

