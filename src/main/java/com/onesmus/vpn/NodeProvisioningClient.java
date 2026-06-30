package com.onesmus.vpn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class NodeProvisioningClient {
    private static final Logger log = LoggerFactory.getLogger(NodeProvisioningClient.class);
    
    public void addPeer(VPnNode node, VPnPeer peer) {
        // In production: send to node via HTTP
        // For development: just log (node doesn't exist yet)
        log.info("Would add peer {} to node {} at {}", peer.getId(), node.getName(), 
                node.getPublicIp() + ":" + node.getApiPort());
    }
    
    public void removePeer(VPnNode node, VPnPeer peer) {
        // In production: send to node via HTTP
        // For development: just log
        log.info("Would remove peer {} from node {}", peer.getId(), node.getName());
    }
    
    private String getNodeToken(VPnNode node) {
        return "node-auth-token";
    }
}