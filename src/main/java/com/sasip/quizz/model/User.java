package com.sasip.quizz.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(nullable = false)
    private String school;

    @Column(nullable = false)
    private Integer alYear;

    @Column(nullable = false)
    private String district;

    private String medium;

    @Column(length = 15, nullable = false)
    private String phone;

    @Column(nullable = true)
    private String email;

    private String username;

    @Column(nullable = false)
    private String passwordHash; // We will exclude this when returning user details

    @Column(nullable = false)
    private Integer earnedXp = 0;

    @Column(nullable = false)
    private Integer streakCount = 0;

    private Double averageScore;

    @Column(nullable = false)
    private Integer totalQuizzesTaken = 0;

    private String parentName;

    private String parentContactNo;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @Column(nullable = false)
    private String userStatus = "active";

    @Column(nullable = false)
    private Integer points = 0;

    // New field to store profile image as Base64 string
    @Column(name = "profile_image_base64", columnDefinition = "LONGTEXT")
    private String profileImageBase64;

    // New field to store FCM token
    @Column(name = "fcm_token", length = 255)
    private String fcmToken; // FCM token field

    // Constructor excluding passwordHash
    public User(Long userId, String username, String role, String firstName, String lastName, String avatarUrl, String school, Integer alYear, String district, String medium, String phone, String email, Integer earnedXp, Integer streakCount, Double averageScore, Integer totalQuizzesTaken, String parentName, String parentContactNo, LocalDateTime createdDate, LocalDateTime updatedDate, String userStatus, Integer points, String profileImageBase64, String fcmToken) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarUrl = avatarUrl;
        this.school = school;
        this.alYear = alYear;
        this.district = district;
        this.medium = medium;
        this.phone = phone;
        this.email = email;
        this.earnedXp = earnedXp;
        this.streakCount = streakCount;
        this.averageScore = averageScore;
        this.totalQuizzesTaken = totalQuizzesTaken;
        this.parentName = parentName;
        this.parentContactNo = parentContactNo;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.userStatus = userStatus;
        this.points = points;
        this.profileImageBase64 = profileImageBase64;
        this.fcmToken = fcmToken; // Assigning fcmToken
    }

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
