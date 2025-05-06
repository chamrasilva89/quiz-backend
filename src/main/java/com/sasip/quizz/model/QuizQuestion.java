package com.sasip.quizz.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", referencedColumnName = "quizId") // Important
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "questionId") // Optional
    private Question question;

    private int sequence; // example field
}