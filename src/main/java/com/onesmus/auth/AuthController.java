package com.onesmus.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AppUserRepository users;
    private final InvitationRepository invites;
    private final SessionRepository sessions;
    private final AuthAppearance appearance;

    public AuthController(AppUserRepository users, InvitationRepository invites,
                          SessionRepository sessions, AuthAppearance appearance) {
        this.users = users;
        this.invites = invites;
        this.sessions = sessions;
        this.appearance = appearance;
    }

    @PostMapping("/appearance")
    public Map<String, Object> updateAppearance(@RequestBody Map<String, Object> body) {
        String primary = (String) body.getOrDefault("primaryColor", "#6366f1");
        String bg = (String) body.getOrDefault("background", "#0a0f1e");
        String logo = (String) body.getOrDefault("logoUrl", null);

        var opts = new AuthAppearance.Options(true, logo, null, null, null, null);
        var vars = new AuthAppearance.Variables(primary, bg, "#ffffff", null, "0.5rem");
        appearance.configure(opts, vars, null);

        return Map.of("status", "updated", "variables", vars);
    }

    @GetMapping("/appearance")
    public Map<String, Object> getAppearance() {
        return Map.of(
                "options", appearance.options(),
                "variables", appearance.variables(),
                "elements", appearance.elements()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> body) {
        String email = body.getOrDefault("email", "").toString();
        String pass = body.getOrDefault("password", "").toString();
        String name = body.getOrDefault("username", "").toString();

        Boolean legalAccepted = (Boolean) body.getOrDefault("legalAccepted", false);
        if (!Boolean.TRUE.equals(legalAccepted))
            return ResponseEntity.badRequest().body(Map.of("error", "Legal acceptance required"));

        if (users.findByEmail(email).isPresent())
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));

        AppUser user = new AppUser(email, pass);
        user.setUsername(name.isEmpty() ? email.split("@")[0] : name);
        user.setFirstName(body.getOrDefault("firstName", "").toString());
        user.setLastName(body.getOrDefault("lastName", "").toString());
        users.save(user);

        return ResponseEntity.ok(Map.of("id", user.getId(), "email", user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
        String email = body.getOrDefault("email", "").toString();
        String pass = body.getOrDefault("password", "").toString();

        var opt = users.findByEmail(email);
        if (opt.isEmpty() || !opt.get().getPassword().equals(pass)) {
            // signUpIfMissing pattern: allow registration with same email
            Boolean signUpIfMissing = (Boolean) body.getOrDefault("signUpIfMissing", false);
            if (Boolean.TRUE.equals(signUpIfMissing)) {
                var user = new AppUser(email, pass);
                user.setUsername(email.split("@")[0]);
                users.save(user);
                return ResponseEntity.ok(Map.of(
                        "authenticated", false,
                        "needs_verification", true,
                        "userId", user.getId(),
                        "sign_up_eligible", true
                ));
            }
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        var user = opt.get();

        if (user.isMfaEnabled()) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", false,
                    "mfa_required", true,
                    "userId", user.getId()
            ));
        }

        String token = UUID.randomUUID().toString();
        var session = new Session(user.getId(), token);
        sessions.save(session);

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "userId", user.getId(),
                "email", user.getEmail(),
                "token", token
        ));
    }

    @PostMapping("/sessions/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestParam String oldToken) {
        var opt = sessions.findByTokenAndStatus(oldToken, "active");
        if (opt.isEmpty() || opt.get().getExpiresAt().isBefore(java.time.LocalDateTime.now()))
            return ResponseEntity.status(401).body(Map.of("error", "Session expired"));

        var session = opt.get();
        session.setToken(UUID.randomUUID().toString());
        session.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(60));
        sessions.save(session);

        return ResponseEntity.ok(Map.of("token", session.getToken()));
    }

    @PostMapping("/invite")
    public ResponseEntity<Map<String, Object>> invite(@RequestBody Map<String, Object> body) {
        String email = body.getOrDefault("email", "").toString();
        String inviter = body.getOrDefault("inviterId", "").toString();

        var existing = invites.findByEmailAndStatus(email, "PENDING");
        if (existing.isPresent())
            return ResponseEntity.ok(Map.of("status", "pending", "token", existing.get().getToken()));

        var invite = new Invitation(email, inviter);
        invites.save(invite);
        return ResponseEntity.ok(Map.of("status", "invited", "token", invite.getToken()));
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<Map<String, Object>> accept(@RequestBody Map<String, Object> body) {
        String token = body.getOrDefault("token", "").toString();
        String pass = body.getOrDefault("password", "").toString();
        String name = body.getOrDefault("username", "").toString();

        var opt = invites.findByTokenAndStatus(token, "PENDING");
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var invite = opt.get();
        var user = new AppUser(invite.getEmail(), pass);
        user.setEmailVerified(true);
        user.setUsername(name.isEmpty() ? invite.getEmail().split("@")[0] : name);
        users.save(user);

        invite.setStatus("ACCEPTED");
        invite.setAcceptedAt(java.time.LocalDateTime.now());
        invites.save(invite);

        return ResponseEntity.ok(Map.of("status", "joined", "userId", user.getId()));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> user(@PathVariable Long userId) {
        var opt = users.findById(userId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        var u = opt.get();
        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "username", u.getUsername(),
                "verified", u.isEmailVerified()
        ));
    }
}