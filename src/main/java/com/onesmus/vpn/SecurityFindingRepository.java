package com.onesmus.vpn;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SecurityFindingRepository extends JpaRepository<SecurityFinding, UUID> {
    List<SecurityFinding> findByNodeIdAndResolvedFalse(UUID nodeId);
    List<SecurityFinding> findTop100ByResolvedFalseOrderBySeverityDescCreatedAtDesc();
}