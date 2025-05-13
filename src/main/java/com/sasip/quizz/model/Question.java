package com.sasip.quizz.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
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
    private Long questionId;

    private String alYear;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @Column(columnDefinition = "JSON")
    private String options;

    private int correctAnswerId; // Renamed

    private String explanation;
    private String subject;
    private String type;
    private String subType;
    private int points;
    private String difficultyLevel;
    private int maxTimeSec;

    private boolean hasAttachment; // New field
    private String module;         // New field
    private String submodule;      // New field

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<QuestionAttachment> attachments;
}