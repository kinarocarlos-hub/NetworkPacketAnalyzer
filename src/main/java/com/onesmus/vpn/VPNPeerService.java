package com.onesmus.vpn;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Base64;

@Service
public class VPNPeerService {
    private final VPNPeerRepository peerRepository;
    private final VPNNodeRepository nodeRepository;
    private final KeyExchangeService keyExchangeService;
    private final NodeProvisioningClient nodeProvisioningClient;
    
    private final Map<String, Collection<Runnable>> revocationCallbacks = new ConcurrentHashMap<>();
    
    public VPNPeerService(VPNPeerRepository peerRepository, VPNNodeRepository nodeRepository,
                          KeyExchangeService keyExchangeService, NodeProvisioningClient nodeProvisioningClient) {
        this.peerRepository = peerRepository;
        this.nodeRepository = nodeRepository;
        this.keyExchangeService = keyExchangeService;
        this.nodeProvisioningClient = nodeProvisioningClient;
    }
    
    @Transactional
    public Map<String, Object> provisionPeer(String userId, CryptoAlgorithm algorithm) {
        VPnNode node = selectNode();
        if (node == null) {
            return Map.of("error", "No available nodes");
        }
        
        KeyPair keyPair;
        try {
            keyPair = keyExchangeService.generateKeyPair(algorithm);
        } catch (Exception e) {
            return Map.of("error", "Failed to generate key pair: " + e.getMessage());
        }
        
        String ip = allocateIP(node);
        
        VPnPeer peer = new VPnPeer(userId, keyExchangeService.encodePublicKey(keyPair.getPublic()),
                node.getId(), ip, algorithm);
        peer.setEncryptedPrivateKey(encryptPrivateKey(algorithm, userId));
        
        peer = peerRepository.save(peer);
        
        nodeProvisioningClient.addPeer(node, peer);
        
        Map<String, Object> result = new HashMap<>();
        result.put("peerId", peer.getId());
        result.put("ipAddress", ip);
        result.put("endpoint", node.getPublicEndpoint());
        result.put("algorithm", algorithm);
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> revokePeer(String userId, UUID peerId) {
        Optional<VPnPeer> optPeer = peerRepository.findById(peerId);
        if (optPeer.isEmpty()) {
            return Map.of("error", "Peer not found");
        }
        
        VPnPeer peer = optPeer.get();
        if (!peer.getUserId().equals(userId) && !userId.equals("admin")) {
            return Map.of("error", "Not authorized");
        }
        
        VPnNode node = nodeRepository.findById(peer.getNodeId()).orElse(null);
        if (node != null) {
            nodeProvisioningClient.removePeer(node, peer);
        }
        
        peer.setStatus(VPnPeer.PeerStatus.REVOKED);
        peer.setRevokedAt(Instant.now());
        peerRepository.save(peer);
        
        notifyRevocationListeners(userId, peerId);
        
        return Map.of("status", "revoked", "peerId", peerId);
    }
    
    public List<VPNPeerView> listActivePeers(String userId) {
        List<VPnPeer> peers = peerRepository.findByUserIdAndStatus(userId, VPnPeer.PeerStatus.ACTIVE);
        
        return peers.stream().map(p -> {
            VPnNode node = nodeRepository.findById(p.getNodeId()).orElse(null);
            return new VPNPeerView(p.getId(), p.getIpAddress(), p.getPublicKey(),
                    node != null ? node.getPublicEndpoint() + ":" + node.getWgPort() : "",
                    p.getCryptoAlgorithm(), node != null ? node.getName() : "unknown");
        }).toList();
    }
    
    public List<VPNSessionView> listActiveSessions(String userId) {
        List<VPnPeer> peers = peerRepository.findByUserIdAndStatus(userId, VPnPeer.PeerStatus.ACTIVE);
        
        return peers.stream().map(p -> new VPNSessionView(
                p.getId(), p.getIpAddress(), p.getBytesTransferred() / 2, 
                p.getBytesTransferred() / 2,
                p.getLastHandshake() != null ? p.getLastHandshake().toString() : "never",
                p.getStatus().name()
        )).toList();
    }
    
    private VPnNode selectNode() {
        return nodeRepository.findRandomOnlineNodeWithCapacity()
                .orElse(nodeRepository.findFirstByStatus(VPnNode.NodeStatus.ONLINE)
                        .orElse(null));
    }
    
    private String allocateIP(VPnNode node) {
        int next = node.getActivePeers() + 1;
        int base = 10;
        return String.format("%s.%d", node.getListenSubnet(), base + next);
    }
    
    private String encryptPrivateKey(CryptoAlgorithm algorithm, String userId) {
        return "encrypted-key-placeholder";
    }
    
    private void notifyRevocationListeners(String userId, UUID peerId) {
        var callbacks = revocationCallbacks.getOrDefault(userId, List.of());
        callbacks.forEach(cb -> cb.run());
    }
}