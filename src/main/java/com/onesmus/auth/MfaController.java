package com.onesmus.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth/mfa")
@CrossOrigin(origins = "*")
public class MfaController {

    private final AppUserRepository users;
    private final Map<String, MfaChallenge> challenges = new ConcurrentHashMap<>();
    private final Random rng = new Random();

    public MfaController(AppUserRepository users) {
        this.users = users;
    }

    @PostMapping("/enable")
    public ResponseEntity<Map<String, Object>> enable(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.getOrDefault("userId", "0"));
        Optional<AppUser> opt = users.findById(userId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        opt.get().setMfaEnabled(true);
        opt.get().setTotpSecret(String.valueOf(rng.nextLong(1000000, 9999999)));
        users.save(opt.get());

        return ResponseEntity.ok(Map.of("enabled", true));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.getOrDefault("userId", "0"));
        String code = body.getOrDefault("code", "");

        var challenge = challenges.get(userId + ":email");
        if (challenge != null && challenge.code.equals(code) && challenge.expiresAt.isAfter(LocalDateTime.now())) {
            return ResponseEntity.ok(Map.of("valid", true));
        }

        Optional<AppUser> opt = users.findById(userId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        if (opt.get().getTotpSecret().equals(code)) {
            return ResponseEntity.ok(Map.of("valid", true));
        }

        return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "Invalid code"));
    }

    @PostMapping("/send-email-code")
    public ResponseEntity<Map<String, Object>> sendEmailCode(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.getOrDefault("userId", "0"));
        String code = String.valueOf(rng.nextInt(900000) + 100000);

        challenges.put(userId + ":email", new MfaChallenge(code, LocalDateTime.now().plusMinutes(5)));
        // In prod: send actual email via SMTP

        return ResponseEntity.ok(Map.of("sent", true));
    }

    private record MfaChallenge(String code, LocalDateTime expiresAt) {}
}