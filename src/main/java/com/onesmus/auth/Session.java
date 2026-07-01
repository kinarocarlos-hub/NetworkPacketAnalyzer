package com.onesmus.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    private String id = UUID.randomUUID().toString();
    
    @Column(name = "user_id")
    private Long userId;
    
    private String token;
    private String status = "active";
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(60);
    private LocalDateTime lastAccessedAt = LocalDateTime.now();
    
    public Session() {}
    public Session(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }
    
    // getters/setters
    public String getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    
    @PreUpdate
    public void updateLastAccessed() {
        this.lastAccessedAt = LocalDateTime.now();
    }
}