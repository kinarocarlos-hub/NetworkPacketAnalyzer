package com.onesmus.vpn;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/node")
public class NodeProvisioningController {
    private final WireGuardAdminService wgAdminService;
    
    public NodeProvisioningController(WireGuardAdminService wgAdminService) {
        this.wgAdminService = wgAdminService;
    }
    
    @PostMapping("/peers")
    public ResponseEntity<Map<String, Object>> addPeer(@RequestBody Map<String, String> request) {
        String peerId = request.get("peerId");
        String publicKey = request.get("publicKey");
        String ipAddress = request.get("ipAddress");
        
        wgAdminService.addPeer(peerId, publicKey, ipAddress);
        
        return ResponseEntity.ok(Map.of("status", "added", "peerId", peerId));
    }
    
    @DeleteMapping("/peers/{peerId}")
    public ResponseEntity<Map<String, Object>> removePeer(@PathVariable String peerId) {
        wgAdminService.removePeer(peerId);
        
        return ResponseEntity.ok(Map.of("status", "removed", "peerId", peerId));
    }
    
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return wgAdminService.getNodeStatus();
    }
}