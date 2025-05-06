package com.sasip.quizz.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id
    private String quizId;

    private String quizName;
    private String intro;

    @Column(columnDefinition = "JSON")
    private String modules;

    private int timeLimit;
    private int xp;
    private int passAccuracy;
    private int alYear;
    private int attemptsAllowed;

    private LocalDateTime scheduledTime;
    private LocalDateTime deadline;

    @Column(columnDefinition = "JSON")
    private String rewardIds;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizQuestion> quizQuestions;
}
