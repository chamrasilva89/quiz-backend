package com.sasip.quizz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_leaderboard")
public class MonthlyLeaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;
    private String school;
    private String district;
    private int alYear;

    private String month; // e.g., "2025-06"

    private int totalPoints;
    private LocalDateTime updatedAt;

    // Getters

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getSchool() {
        return school;
    }

    public String getDistrict() {
        return district;
    }

    public int getAlYear() {
        return alYear;
    }

    public String getMonth() {
        return month;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setAlYear(int alYear) {
        this.alYear = alYear;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}