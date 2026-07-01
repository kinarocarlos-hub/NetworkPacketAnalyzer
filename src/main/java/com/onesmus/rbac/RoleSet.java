package com.onesmus.rbac;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role_sets")
public class RoleSet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private boolean isDefault = false;
    
    @ElementCollection
    @CollectionTable(name = "role_set_roles", joinColumns = @JoinColumn(name = "role_set_id"))
    @Column(name = "role_key")
    private Set<String> roleKeys;
    
    public RoleSet() {}
    public RoleSet(String name, Set<String> roles) {
        this.name = name;
        this.roleKeys = roles;
    }
    
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<String> getRoleKeys() { return roleKeys; }
    public void setRoleKeys(Set<String> roles) { this.roleKeys = roles; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
}