package com.onesmus.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {
    List<OrganizationMember> findByUserId(String userId);
    Optional<OrganizationMember> findByOrgIdAndUserId(Long orgId, String userId);
    List<OrganizationMember> findByOrgId(Long orgId);
}