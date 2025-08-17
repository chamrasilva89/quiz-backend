package com.sasip.quizz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges")
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who earned the badge

    @ManyToOne
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge; // The badge earned by the user

    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt = LocalDateTime.now(); // Timestamp of when the badge was earned

    // Constructors, Getters, Setters, etc.
    public UserBadge() {
    }

    public UserBadge(User user, Badge badge, LocalDateTime earnedAt) {
        this.user = user;
        this.badge = badge;
        this.earnedAt = earnedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }
}
