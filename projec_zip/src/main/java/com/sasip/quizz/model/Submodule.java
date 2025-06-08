package com.sasip.quizz.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submodules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submodule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submoduleId;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
}