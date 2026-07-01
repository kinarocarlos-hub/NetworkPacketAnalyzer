package com.onesmus.rbac;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "org_invitations")
public class OrgInvitation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "org_id")
    private Long orgId;
    
    private String email;
    private String token;
    
    @Enumerated(EnumType.STRING)
    private OrgRole role = OrgRole.MEMBER;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
    private boolean accepted = false;
    
    public OrgInvitation() {}
    public OrgInvitation(Long orgId, String email, OrgRole role) {
        this.orgId = orgId;
        this.email = email;
        this.role = role;
        this.token = java.util.UUID.randomUUID().toString();
    }
    
    // getters/setters
    public Long getId() { return id; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getToken() { return token; }
    public OrgRole getRole() { return role; }
    public void setRole(OrgRole role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
}