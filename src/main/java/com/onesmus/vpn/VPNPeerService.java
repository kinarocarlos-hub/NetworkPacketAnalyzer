package com.onesmus.vpn;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class VPNPeerService {

    private final VPNPeerRepository peerRepo;
    private final VPNNodeRepository nodeRepo;
    private final KeyExchangeService keyService;
    private final NodeProvisioningClient nodeClient;
    private final Map<String, Collection<Runnable>> callbacks = new ConcurrentHashMap<>();

    public VPNPeerService(VPNPeerRepository peerRepo, VPNNodeRepository nodeRepo,
                          KeyExchangeService keyService, NodeProvisioningClient nodeClient) {
        this.peerRepo = peerRepo;
        this.nodeRepo = nodeRepo;
        this.keyService = keyService;
        this.nodeClient = nodeClient;
    }

    @Transactional
    public Map<String, Object> provisionPeer(String userId, CryptoAlgorithm algorithm) {
        VPnNode node = findAvailableNode();
        if (node == null) {
            return Map.of("error", "No VPN nodes available right now");
        }

        KeyPair keys;
        try {
            keys = keyService.generateKeyPair(algorithm);
        } catch (Exception err) {
            return Map.of("error", "Key generation failed: " + err.getMessage());
        }

        String assignedIp = assignIpAddress(node);

        VPnPeer peer = new VPnPeer(userId, keyService.encodePublicKey(keys.getPublic()),
                node.getId(), assignedIp, algorithm);
        peer.setEncryptedPrivateKey(stashPrivateKey(userId));
        peer = peerRepo.save(peer);
        peerRepo.flush();

        nodeClient.addPeer(node, peer);

        Map<String, Object> response = new HashMap<>();
        response.put("peerId", peer.getId());
        response.put("ipAddress", assignedIp);
        response.put("endpoint", node.getPublicEndpoint());
        response.put("algorithm", algorithm.name());
        response.put("nodeName", node.getName());
        return response;
    }

    @Transactional
    public Map<String, Object> revokePeer(String userId, UUID peerId) {
        Optional<VPnPeer> opt = peerRepo.findById(peerId);
        if (opt.isEmpty()) {
            return Map.of("error", "WireGuard peer not found — was it already removed?");
        }

        VPnPeer peer = opt.get();
        if (!peer.getUserId().equals(userId)) {
            return Map.of("error", "Not authorized to revoke this peer");
        }

        VPnNode node = nodeRepo.findById(peer.getNodeId()).orElse(null);
        if (node != null) {
            nodeClient.removePeer(node, peer);
        }

        peer.setStatus(VPnPeer.PeerStatus.REVOKED);
        peer.setRevokedAt(Instant.now());
        peerRepo.save(peer);

        triggerCallbacks(userId, peerId);

        return Map.of("status", "revoked", "peerId", peerId);
    }

    public List<VPNPeerView> listActivePeers(String userId) {
        List<VPnPeer> peers = peerRepo.findByUserIdAndStatus(userId, VPnPeer.PeerStatus.ACTIVE);

        return peers.stream().map(p -> {
            VPnNode n = nodeRepo.findById(p.getNodeId()).orElse(null);
            String endpoint = n != null ? n.getPublicEndpoint() + ":" + n.getWgPort() : "unknown";
            return new VPNPeerView(p.getId(), p.getIpAddress(), p.getPublicKey(), endpoint,
                    p.getCryptoAlgorithm(), n != null ? n.getName() : "unknown");
        }).toList();
    }

    public List<VPNSessionView> listActiveSessions(String userId) {
        List<VPnPeer> peers = peerRepo.findByUserIdAndStatus(userId, VPnPeer.PeerStatus.ACTIVE);

        return peers.stream().map(p -> {
            long bytes = p.getBytesTransferred() != null ? p.getBytesTransferred() : 0L;
            String handshake = p.getLastHandshake() != null ? p.getLastHandshake().toString() : "never";
            return new VPNSessionView(p.getId(), p.getIpAddress(), bytes / 2, bytes / 2, handshake,
                    p.getStatus().name());
        }).toList();
    }

    // Node selection prefers one with capacity, falls back to first online
    private VPnNode findAvailableNode() {
        return nodeRepo.findRandomOnlineNodeWithCapacity()
                .orElse(nodeRepo.findFirstByStatus(VPnNode.NodeStatus.ONLINE).orElse(null));
    }

    String assignIpAddress(VPnNode node) {
        List<VPnPeer> activePeers = peerRepo.findActivePeersByNode(node.getId());
        Set<String> usedIps = activePeers.stream()
                .map(VPnPeer::getIpAddress)
                .collect(Collectors.toSet());

        for (int i = 1; i <= node.getMaxPeers(); i++) {
            String candidate = node.getListenSubnet() + "." + (10 + i);
            if (!usedIps.contains(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("No available IP addresses in subnet");
    }

    private String stashPrivateKey(String userId) {
        // Placeholder - actual implementation would encrypt with user's key
        return "encrypted-key-placeholder";
    }

    private void triggerCallbacks(String userId, UUID peerId) {
        Collection<Runnable> cbs = callbacks.getOrDefault(userId, Collections.emptyList());
        cbs.forEach(Runnable::run);
    }
}