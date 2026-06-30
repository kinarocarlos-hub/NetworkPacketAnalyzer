package com.onesmus.vpn;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class VPNNodeService {
    private final VPNNodeRepository nodeRepository;
    private final VPNPeerRepository peerRepository;
    
    public VPNNodeService(VPNNodeRepository nodeRepository, VPNPeerRepository peerRepository) {
        this.nodeRepository = nodeRepository;
        this.peerRepository = peerRepository;
    }
    
    @Transactional
    public Map<String, Object> registerNode(Map<String, String> request) {
        String name = request.get("name");
        String region = request.get("region");
        String endpoint = request.get("endpoint");
        String publicIp = request.get("publicIp");
        String listenSubnet = request.get("listenSubnet");
        
        Optional<VPnNode> existing = nodeRepository.findAll().stream()
                .filter(n -> n.getName().equals(name))
                .findFirst();
        
        VPnNode node = existing.orElse(new VPnNode(name, region, endpoint, publicIp, listenSubnet));
        node.setStatus(VPnNode.NodeStatus.ONLINE);
        node.setLastHeartbeat(Instant.now());
        
        VPnNode saved = nodeRepository.save(node);
        
        return Map.of(
                "nodeId", saved.getId(),
                "status", "registered",
                "maxPeers", saved.getMaxPeers()
        );
    }
    
    @Transactional
    public Map<String, Object> updateHeartbeat(UUID nodeId, Map<String, Object> request) {
        Optional<VPnNode> opt = nodeRepository.findById(nodeId);
        if (opt.isEmpty()) {
            return Map.of("error", "Unknown node");
        }
        
        VPnNode node = opt.get();
        node.setLastHeartbeat(Instant.now());
        node.setActivePeers(peerRepository.findActivePeersByNode(nodeId).size());
        
        nodeRepository.save(node);
        
        return Map.of("status", "healthy");
    }
    
public List<VPNSecurityNodeView> listPublicNodes() {
        List<VPnNode> nodes = nodeRepository.findAll();
        return nodes.stream()
                .filter(n -> n.getStatus() == VPnNode.NodeStatus.ONLINE)
                .map(n -> new VPNSecurityNodeView(n.getId(), n.getName(), n.getRegion(),
                        n.getPublicEndpoint(), n.getActivePeers(), n.getMaxPeers()))
                .toList();
    }
}