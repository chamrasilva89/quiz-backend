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

    private String school;

    private Integer alYear;

    private String district;

    private String medium;

    @Column(length = 15)
    private String phone;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Integer earnedXp = 0;

    @Column(nullable = false)
    private Integer streakCount = 0;

    // Use scale/precision with BigDecimal if high accuracy is needed
    private Double averageScore;

    @Column(nullable = false)
    private Integer totalQuizzesTaken = 0;

    private String parentName;

    private String parentContactNo;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    // If using Spring JPA auditing, consider:
    // @CreatedDate
    // private LocalDateTime createdDate;
    //
    // @LastModifiedDate
    // private LocalDateTime updatedDate;
}
