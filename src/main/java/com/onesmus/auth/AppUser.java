package com.onesmus.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    
    private boolean emailVerified = false;
    private boolean mfaEnabled = false;
    private String totpSecret;
    
    @ElementCollection
    @CollectionTable(name = "user_public_metadata", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> publicMetadata = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "user_private_metadata", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> privateMetadata = new HashMap<>();
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public AppUser() {}
    public AppUser(String email, String password) { this.email = email; this.password = password; }
    
    // getters/setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean verified) { this.emailVerified = verified; }
    public boolean isMfaEnabled() { return mfaEnabled; }
    public void setMfaEnabled(boolean enabled) { this.mfaEnabled = enabled; }
    public String getTotpSecret() { return totpSecret; }
    public void setTotpSecret(String totpSecret) { this.totpSecret = totpSecret; }
    public Map<String, String> getPublicMetadata() { return publicMetadata; }
    public void setPublicMetadata(Map<String, String> publicMetadata) { this.publicMetadata = publicMetadata; }
    public Map<String, String> getPrivateMetadata() { return privateMetadata; }
    public void setPrivateMetadata(Map<String, String> privateMetadata) { this.privateMetadata = privateMetadata; }
}