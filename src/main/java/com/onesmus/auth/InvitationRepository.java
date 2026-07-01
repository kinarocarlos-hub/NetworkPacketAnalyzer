package com.onesmus.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByTokenAndStatus(String token, String status);
    Optional<Invitation> findByEmailAndStatus(String email, String status);
}