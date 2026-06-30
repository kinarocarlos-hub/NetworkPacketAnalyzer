package com.onesmus.vpn;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vpn_peers", indexes = {
    @Index(name = "idx_peer_node", columnList = "node_id"),
    @Index(name = "idx_peer_user", columnList = "user_id"),
    @Index(name = "idx_peer_status", columnList = "status"),
    @Index(name = "idx_peer_assigned_at", columnList = "assigned_at")
})
public class VPnPeer {
    @Id
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "public_key", nullable = false, length = 1024)
    private String publicKey;
    
    @Column(name = "private_key_enc", nullable = true, length = 2048)
    private String encryptedPrivateKey;
    
    @Column(name = "node_id", nullable = false)
    private UUID nodeId;
    
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
    @Column(name = "crypto_algorithm", nullable = false)
    @Enumerated(EnumType.STRING)
    private CryptoAlgorithm cryptoAlgorithm = CryptoAlgorithm.CURVE25519;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PeerStatus status = PeerStatus.ACTIVE;
    
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;
    
    @Column(name = "revoked_at")
    private Instant revokedAt;
    
    @Column(name = "last_handshake")
    private Instant lastHandshake;
    
    @Column(name = "bytes_transferred")
    private Long bytesTransferred = 0L;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        if (assignedAt == null) assignedAt = Instant.now();
    }
    
    public enum PeerStatus {
        ACTIVE, REVOKED, SUSPENDED
    }
    
    // Constructors
    public VPnPeer() {}
    
    public VPnPeer(String userId, String publicKey, UUID nodeId, String ipAddress, CryptoAlgorithm cryptoAlgorithm) {
        this.userId = userId;
        this.publicKey = publicKey;
        this.nodeId = nodeId;
        this.ipAddress = ipAddress;
        this.cryptoAlgorithm = cryptoAlgorithm;
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    
    public String getEncryptedPrivateKey() { return encryptedPrivateKey; }
    public void setEncryptedPrivateKey(String encryptedPrivateKey) { this.encryptedPrivateKey = encryptedPrivateKey; }
    
    public UUID getNodeId() { return nodeId; }
    public void setNodeId(UUID nodeId) { this.nodeId = nodeId; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public CryptoAlgorithm getCryptoAlgorithm() { return cryptoAlgorithm; }
    public void setCryptoAlgorithm(CryptoAlgorithm cryptoAlgorithm) { this.cryptoAlgorithm = cryptoAlgorithm; }
    
    public PeerStatus getStatus() { return status; }
    public void setStatus(PeerStatus status) { this.status = status; }
    
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
    
    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
    
    public Instant getLastHandshake() { return lastHandshake; }
    public void setLastHandshake(Instant lastHandshake) { this.lastHandshake = lastHandshake; }
    
    public Long getBytesTransferred() { return bytesTransferred; }
    public void setBytesTransferred(Long bytesTransferred) { this.bytesTransferred = bytesTransferred; }
}