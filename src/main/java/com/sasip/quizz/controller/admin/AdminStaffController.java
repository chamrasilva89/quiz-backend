package com.sasip.quizz.controller.admin;

import com.sasip.quizz.dto.*;
import com.sasip.quizz.model.Staff;
import com.sasip.quizz.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/staff")
public class AdminStaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addStaff(@RequestBody StaffRegistrationRequest request) {
        try {
            Staff staff = staffService.addStaff(request);
            // Wrap the staff object in a list to match the desired response format
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", List.of(staff))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateStaff(@PathVariable Long id, @RequestBody StaffUpdateRequest updateRequest) {
        try {
            Staff staff = staffService.updateStaff(id, updateRequest);
            // Wrap the updated staff object in a list
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", List.of(staff))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update staff", 500));
        }
    }

    @PatchMapping("/partial/{id}")
    public ResponseEntity<ApiResponse<?>> updateStaffPartial(@PathVariable Long id, @RequestBody StaffPartialUpdateRequest updateRequest) {
        try {
            Staff staff = staffService.updateStaffPartial(id, updateRequest);
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", List.of(staff))));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Email address already in use", 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update staff", 500));
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getStaffById(@PathVariable Long id) {
        try {
            Staff staff = staffService.getStaffById(id);
            // Wrap the staff object in a list
            return ResponseEntity.ok(new ApiResponse<>(Map.of("items", List.of(staff))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404));
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<?>> getStaffByFilters(
            @RequestBody StaffFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Staff> staffPage = staffService.getStaffByFilters(filterRequest.getRole(), filterRequest.getStatus(), pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("items", staffPage.getContent());  // Return staff data as items array
            response.put("currentPage", staffPage.getNumber());
            response.put("totalItems", staffPage.getTotalElements());
            response.put("totalPages", staffPage.getTotalPages());

            return ResponseEntity.ok(new ApiResponse<>(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to filter staff", 500));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllStaff(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String status
) {
    try {
        Pageable pageable = PageRequest.of(page, size);
        Page<Staff> staffPage = staffService.getStaffByFilters(role, status, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("items", staffPage.getContent());
        response.put("currentPage", staffPage.getNumber());
        response.put("totalItems", staffPage.getTotalElements());
        response.put("totalPages", staffPage.getTotalPages());

        return ResponseEntity.ok(new ApiResponse<>(response));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failed to fetch staff data", 500));
    }
}

}
