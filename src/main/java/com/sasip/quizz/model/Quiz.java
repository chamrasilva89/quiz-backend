package com.sasip.quizz.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quiz")
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

    @Column(name = "question_ids", columnDefinition = "json")
    private String questionIdsJson;

    @Transient
    private List<Long> questionIds;

    public List<Long> getQuestionIds() {
        if (this.questionIds == null && this.questionIdsJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.questionIds = mapper.readValue(this.questionIdsJson, new TypeReference<List<Long>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse question IDs JSON", e);
            }
        }
        return this.questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.questionIdsJson = mapper.writeValueAsString(questionIds);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize question IDs to JSON", e);
        }
    }

}
