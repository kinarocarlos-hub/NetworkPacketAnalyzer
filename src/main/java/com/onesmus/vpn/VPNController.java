package com.onesmus.vpn;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/vpn")
public class VPNController {

    private final VPNPeerService peerSvc;
    private final VPNNodeService nodeSvc;

    public VPNController(VPNPeerService peerSvc, VPNNodeService nodeSvc) {
        this.peerSvc = peerSvc;
        this.nodeSvc = nodeSvc;
    }

    @PostMapping("/peers")
    public ResponseEntity<Map<String, Object>> provision(Principal user, @RequestBody Map<String, String> body) {
        CryptoAlgorithm algo = CryptoAlgorithm.CURVE25519;
        if (body.containsKey("algorithm")) {
            algo = CryptoAlgorithm.valueOf(body.get("algorithm"));
        }
        return ResponseEntity.ok(peerSvc.provisionPeer(user.getName(), algo));
    }

    @PostMapping("/peers/{peerId}/revoke")
    public ResponseEntity<Map<String, Object>> revoke(Principal user, @PathVariable UUID peerId) {
        return ResponseEntity.ok(peerSvc.revokePeer(user.getName(), peerId));
    }

    @GetMapping("/peers")
    public ResponseEntity<List<VPNPeerView>> listPeers(Principal user) {
        return ResponseEntity.ok(peerSvc.listActivePeers(user.getName()));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<VPNSessionView>> sessions(Principal user) {
        return ResponseEntity.ok(peerSvc.listActiveSessions(user.getName()));
    }

    @GetMapping("/nodes")
    public ResponseEntity<List<VPNSecurityNodeView>> nodes() {
        return ResponseEntity.ok(nodeSvc.listPublicNodes());
    }

    @PostMapping("/nodes/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(nodeSvc.registerNode(body));
    }

    @PostMapping("/nodes/{nodeId}/heartbeat")
    public ResponseEntity<Map<String, Object>> heartbeat(@PathVariable UUID nodeId,
                                                          @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(nodeSvc.updateHeartbeat(nodeId, body));
    }
}