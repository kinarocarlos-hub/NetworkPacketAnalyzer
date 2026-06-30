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
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "region", nullable = false)
    private String region;
    
    @Column(name = "public_endpoint", nullable = false)
    private String publicEndpoint;
    
    @Column(name = "wg_port", nullable = false)
    private Integer wgPort = 51820;
    
    @Column(name = "api_port", nullable = false)
    private Integer apiPort = 8080;
    
    @Column(name = "status", nullable = false)
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
    
    @Column(name = "public_ip", nullable = false)
    private String publicIp;
    
    @Column(name = "supported_crypto", nullable = false, length = 1024)
    private String supportedCrypto;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID();
        if (supportedCrypto == null) supportedCrypto = "CURVE25519";
    }
    
    public enum NodeStatus {
        ONLINE, OFFLINE, DRAINING, MAINTENANCE
    }
    
    public VPnNode() {}
    
    public VPnNode(String name, String region, String publicEndpoint, String publicIp, String listenSubnet) {
        this.name = name;
        this.region = region;
        this.publicEndpoint = publicEndpoint;
        this.publicIp = publicIp;
        this.listenSubnet = listenSubnet;
    }
    
    public boolean canAcceptPeer() {
        return status == NodeStatus.ONLINE && activePeers < maxPeers;
    }
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getPublicEndpoint() { return publicEndpoint; }
    public void setPublicEndpoint(String publicEndpoint) { this.publicEndpoint = publicEndpoint; }
    
    public Integer getWgPort() { return wgPort; }
    public void setWgPort(Integer wgPort) { this.wgPort = wgPort; }
    
    public Integer getApiPort() { return apiPort; }
    public void setApiPort(Integer apiPort) { this.apiPort = apiPort; }
    
    public NodeStatus getStatus() { return status; }
    public void setStatus(NodeStatus status) { this.status = status; }
    
    public Instant getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(Instant lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    
    public Integer getActivePeers() { return activePeers; }
    public void setActivePeers(Integer activePeers) { this.activePeers = activePeers; }
    
    public Integer getMaxPeers() { return maxPeers; }
    public void setMaxPeers(Integer maxPeers) { this.maxPeers = maxPeers; }
    
    public String getListenSubnet() { return listenSubnet; }
    public void setListenSubnet(String listenSubnet) { this.listenSubnet = listenSubnet; }
    
    public String getPublicIp() { return publicIp; }
    public void setPublicIp(String publicIp) { this.publicIp = publicIp; }
    
    public String getSupportedCrypto() { return supportedCrypto; }
    public void setSupportedCrypto(String supportedCrypto) { this.supportedCrypto = supportedCrypto; }
}