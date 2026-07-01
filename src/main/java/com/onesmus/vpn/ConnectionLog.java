package com.onesmus.vpn;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "connection_logs", indexes = {
        @Index(name = "idx_conn_peer_time", columnList = "peer_id, timestamp"),
        @Index(name = "idx_conn_timestamp", columnList = "timestamp"),
        @Index(name = "idx_conn_node", columnList = "node_id")
})
public class ConnectionLog {

    @Id
    private UUID id;

    @Column(name = "peer_id", nullable = false)
    private UUID peerId;

    @Column(name = "node_id", nullable = false)
    private UUID nodeId;

    @Column(name = "duration_seconds")
    private Long durationSeconds = 0L;

    @Column(name = "bytes_up")
    private Long bytesUp = 0L;

    @Column(name = "bytes_down")
    private Long bytesDown = 0L;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "log_date", nullable = false)
    private String logDate;

    @Column(name = "user_id_hash")
    private String userIdHash;

    @PrePersist
    void init() {
        if (id == null) id = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
        if (logDate == null) logDate = timestamp.atZone(ZoneOffset.UTC).toString().substring(0, 10);
    }

    public ConnectionLog() {}

    public ConnectionLog(UUID peerId, UUID nodeId) {
        this.peerId = peerId;
        this.nodeId = nodeId;
    }

    // Getters/setters
    public UUID getId() { return id; }
    public UUID getPeerId() { return peerId; }
    public void setPeerId(UUID peerId) { this.peerId = peerId; }
    public UUID getNodeId() { return nodeId; }
    public void setNodeId(UUID nodeId) { this.nodeId = nodeId; }
    public Long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Long secs) { this.durationSeconds = secs; }
    public Long getBytesUp() { return bytesUp; }
    public void setBytesUp(Long b) { this.bytesUp = b; }
    public Long getBytesDown() { return bytesDown; }
    public void setBytesDown(Long b) { this.bytesDown = b; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant ts) { this.timestamp = ts; }
    public String getLogDate() { return logDate; }
    public String getUserIdHash() { return userIdHash; }
    public void setUserIdHash(String hash) { this.userIdHash = hash; }
}