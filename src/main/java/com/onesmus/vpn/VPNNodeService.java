package com.onesmus.vpn;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class VPNNodeService {

    private final VPNNodeRepository nodeRepo;
    private final VPNPeerRepository peerRepo;

    public VPNNodeService(VPNNodeRepository nodeRepo, VPNPeerRepository peerRepo) {
        this.nodeRepo = nodeRepo;
        this.peerRepo = peerRepo;
    }

    @Transactional
    public Map<String, Object> registerNode(Map<String, String> req) {
        String name = req.get("name");
        String region = req.get("region");
        String endpoint = req.get("endpoint");
        String publicIp = req.get("publicIp");
        String subnet = req.get("listenSubnet");

        Optional<VPnNode> existing = nodeRepo.findAll().stream()
                .filter(n -> n.getName().equals(name))
                .findFirst();

        VPnNode node = existing.orElse(new VPnNode(name, region, endpoint, publicIp, subnet));
        node.setStatus(VPnNode.NodeStatus.ONLINE);
        node.setLastHeartbeat(Instant.now());

        VPnNode saved = nodeRepo.save(node);

        Map<String, Object> response = new HashMap<>();
        response.put("nodeId", saved.getId());
        response.put("status", "registered");
        response.put("maxPeers", saved.getMaxPeers());
        return response;
    }

    @Transactional
    public Map<String, Object> updateHeartbeat(UUID nodeId, Map<String, Object> req) {
        Optional<VPnNode> opt = nodeRepo.findById(nodeId);
        if (opt.isEmpty()) {
            return Map.of("error", "Unknown node ID: " + nodeId);
        }

        VPnNode node = opt.get();
        node.setLastHeartbeat(Instant.now());
        // TODO: use count query instead of loading all peers
        node.setActivePeers((int) peerRepo.findActivePeersByNode(nodeId).stream().count());
        nodeRepo.save(node);

        return Map.of("status", "healthy");
    }

    public List<VPNSecurityNodeView> listPublicNodes() {
        return nodeRepo.findAll().stream()
                .filter(n -> n.getStatus() == VPnNode.NodeStatus.ONLINE)
                .map(n -> new VPNSecurityNodeView(n.getId(), n.getName(), n.getRegion(),
                        n.getPublicEndpoint(), n.getActivePeers(), n.getMaxPeers()))
                .toList();
    }
}