package com.onesmus.vpn;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, UUID> {
    List<ConnectionLog> findTop100ByPeerIdOrderByTimestampDesc(UUID peerId);
}