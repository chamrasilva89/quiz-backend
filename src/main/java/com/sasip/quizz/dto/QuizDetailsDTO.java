package com.sasip.quizz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDetailsDTO {

    private Long id; // Quiz ID
    private String name; // Quiz name
    private Integer alYear; // AL Year (Integer)
    private long activeStudents; // Active students count
    private long completedStudents; // Completed students count
}
