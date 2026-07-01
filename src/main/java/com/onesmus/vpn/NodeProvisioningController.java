package com.onesmus.vpn;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/node")
public class NodeProvisioningController {

    private final WireGuardAdminService wg;

    public NodeProvisioningController(WireGuardAdminService wg) {
        this.wg = wg;
    }

    // Called by control plane to provision a new peer on this node
    @PostMapping("/peers")
    public ResponseEntity<Map<String, Object>> add(@RequestBody Map<String, String> body) {
        wg.addPeer(body.get("peerId"), body.get("publicKey"), body.get("ipAddress"));
        return ResponseEntity.ok(Map.of("status", "added", "peerId", body.get("peerId")));
    }

    @DeleteMapping("/peers/{peerId}")
    public ResponseEntity<Map<String, Object>> remove(@PathVariable String peerId) {
        wg.removePeer(peerId);
        return ResponseEntity.ok(Map.of("status", "removed", "peerId", peerId));
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return wg.getNodeStatus();
    }
}