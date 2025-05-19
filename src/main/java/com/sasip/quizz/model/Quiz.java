package com.sasip.quizz.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long quizId;
    
    private String quizName;
    private String intro;

    @Column(columnDefinition = "JSON")
    private String modules;

    private int timeLimit;
    private int xp;
    private int passAccuracy;
    private String alYear;
    private int attemptsAllowed;

    private LocalDateTime scheduledTime;
    private LocalDateTime deadline;

    @Column(columnDefinition = "JSON")
    private String rewardIds;

    @Column(name = "question_ids", columnDefinition = "json")
    private String questionIdsJson;

    @Transient
    private List<Long> questionIds;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type")
    private QuizType quizType;

    @Column(name = "user_id")
    private Long userId;

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

    @Transient
    private List<String> moduleList;

    public List<String> getModuleList() {
        if (this.moduleList == null && this.modules != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.moduleList = mapper.readValue(this.modules, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse modules JSON", e);
            }
        }
        return this.moduleList;
    }

    public void setModuleList(List<String> moduleList) {
        this.moduleList = moduleList;
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.modules = mapper.writeValueAsString(moduleList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize modules to JSON", e);
        }
    }
    @Transient
    private List<Long> rewardIdList;
    
    public List<Long> getRewardIdList() {
        if (this.rewardIdList == null && this.rewardIds != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.rewardIdList = mapper.readValue(this.rewardIds, new TypeReference<List<Long>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse reward IDs JSON", e);
            }
        }
        return this.rewardIdList;
    }
    
    public void setRewardIdList(List<Long> rewardIdList) {
        this.rewardIdList = rewardIdList;
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.rewardIds = mapper.writeValueAsString(rewardIdList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize reward IDs to JSON", e);
        }
    }
    
}
