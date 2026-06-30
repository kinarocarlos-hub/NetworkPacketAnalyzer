package com.onesmus.vpn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VPNNodeRepository extends JpaRepository<VPnNode, UUID> {
    Optional<VPnNode> findFirstByStatus(VPnNode.NodeStatus status);
    
    @Query("SELECT n FROM VPnNode n WHERE n.status = 'ONLINE' AND n.activePeers < n.maxPeers ORDER BY RAND() LIMIT 1")
    Optional<VPnNode> findRandomOnlineNodeWithCapacity();
}