package com.onesmus.rbac;

import jakarta.persistence.*;
import java.util.Map;
import java.util.HashMap;

@Entity
@Table(name = "organizations")
public class Organization {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String name;
    
    private String slug;
    
    @ElementCollection
    @CollectionTable(name = "org_public_metadata", joinColumns = @JoinColumn(name = "org_id"))
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> publicMetadata = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "org_private_metadata", joinColumns = @JoinColumn(name = "org_id"))
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> privateMetadata = new HashMap<>();
    
    public Organization() {}
    public Organization(String name, String slug) { this.name = name; this.slug = slug; }
    
    // getters/setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public Map<String, String> getPublicMetadata() { return publicMetadata; }
    public void setPublicMetadata(Map<String, String> publicMetadata) { this.publicMetadata = publicMetadata; }
    public Map<String, String> getPrivateMetadata() { return privateMetadata; }
    public void setPrivateMetadata(Map<String, String> privateMetadata) { this.privateMetadata = privateMetadata; }
}