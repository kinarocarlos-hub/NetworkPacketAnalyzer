package com.onesmus.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface OrgInvitationRepository extends JpaRepository<OrgInvitation, Long> {
    Optional<OrgInvitation> findByToken(String token);
    List<OrgInvitation> findByOrgId(Long orgId);
    Optional<OrgInvitation> findByEmailAndOrgId(String email, Long orgId);
}