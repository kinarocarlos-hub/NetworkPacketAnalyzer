package com.onesmus.vpn;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vpn_nodes", indexes = {
        @Index(name = "idx_node_status", columnList = "status"),
        @Index(name = "idx_node_region", columnList = "region")
})
public class VPnNode {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String region;

    @Column(name = "public_endpoint", nullable = false)
    private String publicEndpoint;

    @Column(name = "public_ip", nullable = false)
    private String publicIp;

    @Column(name = "wg_port", nullable = false)
    private Integer wgPort = 51820;

    @Column(name = "api_port", nullable = false)
    private Integer apiPort = 8080;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NodeStatus status = NodeStatus.ONLINE;

    @Column(name = "last_heartbeat")
    private Instant lastHeartbeat;

    @Column(name = "active_peers")
    private Integer activePeers = 0;

    @Column(name = "max_peers")
    private Integer maxPeers = 1000;

    @Column(name = "listen_subnet", nullable = false)
    private String listenSubnet;

    @Column(name = "supported_crypto", nullable = false, length = 1024)
    private String supportedCrypto;

    public enum NodeStatus {
        ONLINE, OFFLINE, DRAINING, MAINTENANCE
    }

    public VPnNode() {}

    public VPnNode(String name, String region, String endpoint, String ip, String subnet) {
        this.name = name;
        this.region = region;
        this.publicEndpoint = endpoint;
        this.publicIp = ip;
        this.listenSubnet = subnet;
    }

    @PrePersist
    void genId() {
        if (id == null) id = UUID.randomUUID();
        if (supportedCrypto == null) supportedCrypto = "CURVE25519";
    }

    public boolean hasCapacity() {
        return status == NodeStatus.ONLINE && activePeers < maxPeers;
    }

    // Getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getPublicEndpoint() { return publicEndpoint; }
    public void setPublicEndpoint(String endpoint) { this.publicEndpoint = endpoint; }
    public String getPublicIp() { return publicIp; }
    public void setPublicIp(String ip) { this.publicIp = ip; }
    public Integer getWgPort() { return wgPort; }
    public void setWgPort(Integer port) { this.wgPort = port; }
    public Integer getApiPort() { return apiPort; }
    public void setApiPort(Integer port) { this.apiPort = port; }
    public NodeStatus getStatus() { return status; }
    public void setStatus(NodeStatus status) { this.status = status; }
    public Instant getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(Instant ts) { this.lastHeartbeat = ts; }
    public Integer getActivePeers() { return activePeers; }
    public void setActivePeers(Integer count) { this.activePeers = count; }
    public Integer getMaxPeers() { return maxPeers; }
    public void setMaxPeers(Integer max) { this.maxPeers = max; }
    public String getListenSubnet() { return listenSubnet; }
    public void setListenSubnet(String subnet) { this.listenSubnet = subnet; }
    public String getSupportedCrypto() { return supportedCrypto; }
    public void setSupportedCrypto(String algos) { this.supportedCrypto = algos; }
}