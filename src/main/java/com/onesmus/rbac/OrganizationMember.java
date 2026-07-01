package com.onesmus.rbac;

import jakarta.persistence.*;

@Entity
@Table(name = "organization_members")
public class OrganizationMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "org_id")
    private Long orgId;
    
    @Column(name = "user_id")
    private String userId;
    
    @Enumerated(EnumType.STRING)
    private OrgRole role = OrgRole.MEMBER;
    
    public OrganizationMember() {}
    public OrganizationMember(Long orgId, String userId, OrgRole role) {
        this.orgId = orgId;
        this.userId = userId;
        this.role = role;
    }
    
    public Long getId() { return id; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public OrgRole getRole() { return role; }
    public void setRole(OrgRole role) { this.role = role; }
}