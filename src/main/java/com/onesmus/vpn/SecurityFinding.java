package com.onesmus.vpn;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_findings", indexes = {
        @Index(name = "idx_finding_node", columnList = "node_id"),
        @Index(name = "idx_finding_type", columnList = "finding_type"),
        @Index(name = "idx_finding_severity", columnList = "severity"),
        @Index(name = "idx_finding_time", columnList = "created_at")
})
public class SecurityFinding {

    @Id
    private UUID id;

    @Column(name = "node_id", nullable = false)
    private UUID nodeId;

    @Column(name = "finding_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FindingType type;

    @Column(name = "severity", nullable = false)
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "raw_output", length = 65535)
    private String rawOutput;

    @Column(name = "port")
    private Integer port;

    @Column(name = "cve_id")
    private String cveId;

    @Column(name = "resolved", nullable = false)
    private boolean resolved = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    public enum FindingType {
        OPEN_PORT, VULNERABILITY, MALICIOUS_IP, TRAFFIC_SPIKE, PORT_SCAN, PROTOCOL_ANOMALY
    }

    public enum Severity {
        CRITICAL, HIGH, MEDIUM, LOW, INFO
    }

    public SecurityFinding() {}

    public SecurityFinding(UUID nodeId, FindingType type, Severity severity, String title) {
        this.nodeId = nodeId;
        this.type = type;
        this.severity = severity;
        this.title = title;
    }

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }

    // Getters/setters
    public UUID getId() { return id; }
    public UUID getNodeId() { return nodeId; }
    public void setNodeId(UUID nodeId) { this.nodeId = nodeId; }
    public FindingType getFindingType() { return type; }
    public void setFindingType(FindingType type) { this.type = type; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRawOutput() { return rawOutput; }
    public void setRawOutput(String rawOutput) { this.rawOutput = rawOutput; }
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    public String getCveId() { return cveId; }
    public void setCveId(String cveId) { this.cveId = cveId; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}