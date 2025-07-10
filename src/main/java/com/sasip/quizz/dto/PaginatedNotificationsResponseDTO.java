package com.sasip.quizz.dto;

import java.util.List;

public class PaginatedNotificationsResponseDTO {
    private List<NotificationResponseDTO> items;
    private int currentPage;
    private long totalItems;
    private int totalPages;

    // Getters and setters
    public List<NotificationResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<NotificationResponseDTO> items) {
        this.items = items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
