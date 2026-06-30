package com.onesmus.vpn;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/vpn")
public class VPNController {
    private final VPNPeerService peerService;
    private final VPNNodeService nodeService;
    
    public VPNController(VPNPeerService peerService, VPNNodeService nodeService) {
        this.peerService = peerService;
        this.nodeService = nodeService;
    }
    
    @PostMapping("/peers")
    public ResponseEntity<Map<String, Object>> provisionPeer(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user,
            @RequestBody Map<String, String> request) {
        
        CryptoAlgorithm algorithm = CryptoAlgorithm.CURVE25519;
        if (request.containsKey("algorithm")) {
            algorithm = CryptoAlgorithm.valueOf(request.get("algorithm"));
        }
        
        return ResponseEntity.ok(peerService.provisionPeer(user.getUsername(), algorithm));
    }
    
    @PostMapping("/peers/{peerId}/revoke")
    public ResponseEntity<Map<String, Object>> revokePeer(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user,
            @PathVariable UUID peerId) {
        
        return ResponseEntity.ok(peerService.revokePeer(user.getUsername(), peerId));
    }
    
    @GetMapping("/peers")
    public ResponseEntity<List<VPNPeerView>> listActivePeers(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user) {
        
        return ResponseEntity.ok(peerService.listActivePeers(user.getUsername()));
    }
    
    @GetMapping("/sessions")
    public ResponseEntity<List<VPNSessionView>> listActiveSessions(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails user) {
        
        return ResponseEntity.ok(peerService.listActiveSessions(user.getUsername()));
    }
    
    @GetMapping("/nodes")
    public ResponseEntity<List<VPNSecurityNodeView>> listNodes() {
        return ResponseEntity.ok(nodeService.listPublicNodes());
    }
    
    @PostMapping("/nodes/register")
    public ResponseEntity<Map<String, Object>> registerNode(
            @RequestBody Map<String, String> request) {
        
        return ResponseEntity.ok(nodeService.registerNode(request));
    }
    
    @PostMapping("/nodes/{nodeId}/heartbeat")
    public ResponseEntity<Map<String, Object>> nodeHeartbeat(
            @PathVariable UUID nodeId,
            @RequestBody Map<String, Object> request) {
        
        return ResponseEntity.ok(nodeService.updateHeartbeat(nodeId, request));
    }
}