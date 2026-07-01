package com.onesmus.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_invitations")
public class Invitation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String token;
    private String inviterId;
    private String status = "PENDING";
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
    private LocalDateTime acceptedAt;
    
    public Invitation() {}
    public Invitation(String email, String inviterId) {
        this.email = email;
        this.inviterId = inviterId;
        this.token = java.util.UUID.randomUUID().toString();
    }
    
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getToken() { return token; }
    public String getInviterId() { return inviterId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
}