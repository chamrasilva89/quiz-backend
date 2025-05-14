package com.sasip.quizz.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
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

    @Column(columnDefinition = "json")
    private String options;

    private Long correctAnswerId; 

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

        // transient for easier access in code
    @Transient
    private List<String> optionsList;

    public void setOptionsList(List<String> optionsList) {
        this.optionsList = optionsList;
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.options = mapper.writeValueAsString(optionsList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize options list", e);
        }
    }

    public List<String> getOptionsList() {
        if (this.options != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(this.options, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize options JSON", e);
            }
        }
        return null;
    }

    // Optional: fallback getter/setter
    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}