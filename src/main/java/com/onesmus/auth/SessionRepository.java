package com.onesmus.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, String> {
    Optional<Session> findByTokenAndStatus(String token, String status);
    Optional<Session> findByUserIdAndStatus(Long userId, String status);
    Optional<Session> findByToken(String token);
}