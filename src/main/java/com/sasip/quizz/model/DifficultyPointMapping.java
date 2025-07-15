package com.sasip.quizz.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "difficulty_point_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DifficultyPointMapping {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "level", length = 20)
    private DifficultyLevel level;

    @Column(name = "points", nullable = false)
    private Integer points;
}
