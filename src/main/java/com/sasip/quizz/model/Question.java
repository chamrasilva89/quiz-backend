package com.sasip.quizz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  questionId;

    private String quizId;
    private String questionText;

    @Column(columnDefinition = "JSON")
    private String options; // store JSON array as string

    private int correctOptionIndex;
    private String explanation;
    private String subject;
    private String type;
    private String subType;
    private int points;
    private String difficultyLevel;
    private int maxTimeSec;
}
