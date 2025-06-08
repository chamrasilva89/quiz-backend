package com.sasip.quizz.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id") // Matches your DB column
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String name;
}

