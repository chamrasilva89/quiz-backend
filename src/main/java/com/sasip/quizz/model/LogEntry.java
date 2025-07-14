package com.sasip.quizz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level;           // INFO, ERROR, WARN
    private String source;          // Controller/Service name or class
    private String action;          // What happened
    private String performedBy;     // Username or userId

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime timestamp;

   @Column(columnDefinition = "LONGTEXT")
    private String previousValue;   // JSON or serialized text

    @Column(columnDefinition = "LONGTEXT")
    private String newValue;        // JSON or serialized text

    private String entity;          // e.g., "User", "RewardWinner"

    private String section;         // e.g., "AdminPanel", "QuizManagement"
}