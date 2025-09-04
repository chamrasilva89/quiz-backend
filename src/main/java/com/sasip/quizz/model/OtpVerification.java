package com.sasip.quizz.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "otp_verification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This can be null initially, as the user is not yet created.
    private Long userId;

    private String phone;
    private String otp;
    private LocalDateTime expiresAt;
    private boolean verified;
}