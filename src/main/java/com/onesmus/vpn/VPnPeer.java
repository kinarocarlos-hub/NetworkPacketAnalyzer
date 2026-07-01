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

    @Column(name = "private_key_enc", length = 2048)
    private String encryptedPrivateKey;

    @Column(name = "node_id", nullable = false)
    private UUID nodeId;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "crypto_algorithm", nullable = false)
    @Enumerated(EnumType.STRING)
    private CryptoAlgorithm algorithm = CryptoAlgorithm.CURVE25519;

    @Column(nullable = false)
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

    public enum PeerStatus {
        ACTIVE, REVOKED, SUSPENDED
    }

    public VPnPeer() {}

    public VPnPeer(String userId, String publicKey, UUID nodeId, String ip, CryptoAlgorithm algo) {
        this.userId = userId;
        this.publicKey = publicKey;
        this.nodeId = nodeId;
        this.ipAddress = ip;
        this.algorithm = algo;
    }

    @PrePersist
    void init() {
        if (id == null) id = UUID.randomUUID();
        if (assignedAt == null) assignedAt = Instant.now();
    }

    // Getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    public String getEncryptedPrivateKey() { return encryptedPrivateKey; }
    public void setEncryptedPrivateKey(String enc) { this.encryptedPrivateKey = enc; }
    public UUID getNodeId() { return nodeId; }
    public void setNodeId(UUID nodeId) { this.nodeId = nodeId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ip) { this.ipAddress = ip; }
    public CryptoAlgorithm getCryptoAlgorithm() { return algorithm; }
    public void setCryptoAlgorithm(CryptoAlgorithm algorithm) { this.algorithm = algorithm; }
    public PeerStatus getStatus() { return status; }
    public void setStatus(PeerStatus status) { this.status = status; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant ts) { this.assignedAt = ts; }
    public Instant getRevokedAt() { return revokedAt; }
    public void setRevokedAt(Instant ts) { this.revokedAt = ts; }
    public Instant getLastHandshake() { return lastHandshake; }
    public void setLastHandshake(Instant ts) { this.lastHandshake = ts; }
    public Long getBytesTransferred() { return bytesTransferred; }
    public void setBytesTransferred(Long bytes) { this.bytesTransferred = bytes; }
}