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

    @Column(length = 15,nullable = false )
    private String phone;

   @Column(nullable = true) // or just omit nullable
    private String email;


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

    @Column(nullable = false)
    private String userStatus = "active";

    @Column(nullable = false)
    private Integer points = 0;


    // If using Spring JPA auditing, consider:
    // @CreatedDate
    // private LocalDateTime createdDate;
    //
    // @LastModifiedDate
    // private LocalDateTime updatedDate;
}
