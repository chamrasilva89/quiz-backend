package com.sasip.quizz.dto;

import java.util.List;

public class PaginatedALYearResponseDTO {

    private int totalItems;
    private int totalPages;
    private int currentPage;
    private List<ALYearResponseDTO> items;

    // Getters and Setters
    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<ALYearResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<ALYearResponseDTO> items) {
        this.items = items;
    }
}
