package com.onesmus.vpn;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, UUID> {
    List<ConnectionLog> findTop100ByPeerIdOrderByTimestampDesc(UUID peerId);
}