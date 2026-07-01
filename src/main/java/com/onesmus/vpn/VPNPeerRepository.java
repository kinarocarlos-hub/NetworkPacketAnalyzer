package com.onesmus.vpn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface VPNPeerRepository extends JpaRepository<VPnPeer, UUID> {
    List<VPnPeer> findByUserIdAndStatus(String userId, VPnPeer.PeerStatus status);

    @Query("SELECT p FROM VPnPeer p WHERE p.nodeId = :nodeId AND p.status = 'ACTIVE'")
    List<VPnPeer> findActivePeersByNode(UUID nodeId);

    List<VPnPeer> findByNodeId(UUID nodeId);
}