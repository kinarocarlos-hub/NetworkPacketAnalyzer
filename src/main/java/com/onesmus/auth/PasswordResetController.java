package com.onesmus.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class PasswordResetController {

    private final AppUserRepository users;
    private final SessionRepository sessions;

    // Store reset tokens (in prod, use Redis or database)
    private final Map<String, ResetRequest> pendingResets = new ConcurrentHashMap<>();

    public PasswordResetController(AppUserRepository users, SessionRepository sessions) {
        this.users = users;
        this.sessions = sessions;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> requestReset(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "");
        Optional<AppUser> opt = users.findByEmail(email);

        if (opt.isEmpty()) {
            // Don't reveal if email exists
            return ResponseEntity.ok(Map.of("status", "sent"));
        }

        String token = UUID.randomUUID().toString();
        pendingResets.put(token, new ResetRequest(opt.get().getId(), LocalDateTime.now().plusMinutes(30)));

        // In prod: send email with reset link containing token
        return ResponseEntity.ok(Map.of("status", "sent", "token", token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> reset(@RequestBody Map<String, String> body) {
        String token = body.getOrDefault("token", "");
        String newPass = body.getOrDefault("password", "");

        ResetRequest req = pendingResets.get(token);
        if (req == null || req.expiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token"));
        }

        users.findById(req.userId()).ifPresent(u -> {
            u.setPassword(newPass);
            users.save(u);
        });

        // Revoke all existing sessions
        sessions.findAll().stream()
                .filter(s -> s.getUserId().equals(req.userId()))
                .forEach(s -> {
                    s.setStatus("revoked");
                    sessions.save(s);
                });

        pendingResets.remove(token);
        return ResponseEntity.ok(Map.of("status", "reset"));
    }

    private record ResetRequest(Long userId, LocalDateTime expiresAt) {}
}