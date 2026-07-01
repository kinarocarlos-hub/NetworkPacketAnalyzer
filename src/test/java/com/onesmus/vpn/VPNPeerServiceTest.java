package com.onesmus.vpn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VPNPeerServiceTest {

    @Mock
    private VPNPeerRepository peerRepo;

    @Mock
    private VPNNodeRepository nodeRepo;

    @Mock
    private KeyExchangeService keyService;

    private final NodeProvisioningClient nodeClient = new NodeProvisioningClient();

    @Test
    void provisionPeer_success() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256);
        KeyPair keys = kpg.generateKeyPair();
        when(keyService.generateKeyPair(any())).thenReturn(keys);
        when(keyService.encodePublicKey(keys.getPublic())).thenReturn("pk");

        VPnNode node = new VPnNode("node1", "us-east", "ep", "1.2.3.4", "10.0.0");
        node.setMaxPeers(10);
        when(nodeRepo.findRandomOnlineNodeWithCapacity()).thenReturn(Optional.of(node));
        when(peerRepo.findActivePeersByNode(node.getId())).thenReturn(List.of());
        when(peerRepo.save(any(VPnPeer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VPNPeerService svc = new VPNPeerService(peerRepo, nodeRepo, keyService, nodeClient);

        Map<String, Object> result = svc.provisionPeer("user1", CryptoAlgorithm.CURVE25519);

        assertThat(result).containsEntry("ipAddress", "10.0.0.11");
        assertThat(result).containsEntry("nodeName", "node1");
    }

    @Test
    void provisionPeer_noNodesAvailable() {
        VPNPeerService svc = new VPNPeerService(peerRepo, nodeRepo, keyService, nodeClient);
        when(nodeRepo.findRandomOnlineNodeWithCapacity()).thenReturn(Optional.empty());
        when(nodeRepo.findFirstByStatus(any())).thenReturn(Optional.empty());

        Map<String, Object> result = svc.provisionPeer("user1", CryptoAlgorithm.CURVE25519);
        assertThat(result).containsEntry("error", "No VPN nodes available right now");
    }

    @Test
    void revokePeer_notFound() {
        VPNPeerService svc = new VPNPeerService(peerRepo, nodeRepo, keyService, nodeClient);
        when(peerRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

        Map<String, Object> result = svc.revokePeer("user1", UUID.randomUUID());
        assertThat(result).containsEntry("error", "WireGuard peer not found — was it already removed?");
    }

    @Test
    void revokePeer_unauthorized() {
        VPnPeer peer = new VPnPeer();
        peer.setUserId("otherUser");
        when(peerRepo.findById(any(UUID.class))).thenReturn(Optional.of(peer));

        VPNPeerService svc = new VPNPeerService(peerRepo, nodeRepo, keyService, nodeClient);
        Map<String, Object> result = svc.revokePeer("user1", UUID.randomUUID());
        assertThat(result).containsEntry("error", "Not authorized to revoke this peer");
    }

    @Test
    void revokePeer_success() {
        UUID peerId = UUID.randomUUID();
        VPnPeer peer = new VPnPeer("user1", "pk", UUID.randomUUID(), "10.0.0.2", CryptoAlgorithm.CURVE25519);
        peer.setId(peerId);
        when(peerRepo.findById(peerId)).thenReturn(Optional.of(peer));
        when(nodeRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

        VPNPeerService svc = new VPNPeerService(peerRepo, nodeRepo, keyService, nodeClient);
        Map<String, Object> result = svc.revokePeer("user1", peerId);

        assertThat(result).containsEntry("status", "revoked");
        verify(peerRepo).save(peer);
    }

    @Test
    void assignIpAddress_skipsUsedIps() {
        VPnNode node = new VPnNode("node1", "us-east", "ep", "1.2.3.4", "10.0.0");
        node.setMaxPeers(5);
        VPNPeerService svc = new VPNPeerService(peerRepo, nodeRepo, keyService, nodeClient);

        VPnPeer used = new VPnPeer("user1", "pk", node.getId(), "10.0.0.11", CryptoAlgorithm.CURVE25519);
        when(peerRepo.findActivePeersByNode(node.getId())).thenReturn(List.of(used));

        String ip = svc.assignIpAddress(node);
        assertThat(ip).isEqualTo("10.0.0.12");
    }

    @Test
    void assignIpAddress_throwsWhenExhausted() {
        VPnNode node = new VPnNode("node1", "us-east", "ep", "1.2.3.4", "10.0.0");
        node.setMaxPeers(2);
        VPNPeerService svc = new VPNPeerService(peerRepo, nodeRepo, keyService, nodeClient);

        VPnPeer used1 = new VPnPeer("u1", "pk", node.getId(), "10.0.0.11", CryptoAlgorithm.CURVE25519);
        VPnPeer used2 = new VPnPeer("u2", "pk", node.getId(), "10.0.0.12", CryptoAlgorithm.CURVE25519);
        when(peerRepo.findActivePeersByNode(node.getId())).thenReturn(List.of(used1, used2));

        assertThrows(IllegalStateException.class, () -> svc.assignIpAddress(node));
    }
}
