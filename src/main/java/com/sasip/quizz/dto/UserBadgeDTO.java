package com.sasip.quizz.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserBadgeDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private List<BadgeDTO> badges;
}