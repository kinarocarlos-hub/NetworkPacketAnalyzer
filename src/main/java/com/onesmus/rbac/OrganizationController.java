package com.onesmus.rbac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/org")
@CrossOrigin(origins = "*")
public class OrganizationController {

    @Autowired OrganizationRepository orgRepo;
    @Autowired OrganizationMemberRepository memberRepo;
    @Autowired OrgInvitationRepository inviteRepo;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrg(@RequestParam String name, @RequestParam String userId) {
        Organization org = new Organization(name, name.toLowerCase().replace(" ", "-"));
        org = orgRepo.save(org);
        OrganizationMember member = new OrganizationMember(org.getId(), userId, OrgRole.ADMIN);
        memberRepo.save(member);
        return ResponseEntity.ok(Map.of("id", org.getId(), "name", org.getName(), "slug", org.getSlug()));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listOrgs(@RequestParam String userId) {
        List<OrganizationMember> memberships = memberRepo.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (OrganizationMember m : memberships) {
            Organization org = orgRepo.findById(m.getOrgId()).orElse(null);
            if (org != null) {
                result.add(Map.of("id", org.getId(), "name", org.getName(), "slug", org.getSlug(), "role", m.getRole().toString()));
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/switch")
    public ResponseEntity<Map<String, Object>> switchOrg(@RequestParam Long orgId, @RequestParam String userId) {
        Optional<OrganizationMember> membership = memberRepo.findByOrgIdAndUserId(orgId, userId);
        if (membership.isEmpty()) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(Map.of("activeOrgId", orgId, "role", membership.get().getRole().toString()));
    }

    @PostMapping("/invite")
    public ResponseEntity<Map<String, Object>> invite(@RequestParam Long orgId, @RequestParam String email, @RequestParam String role) {
        OrgRole orgRole = OrgRole.MEMBER;
        try { orgRole = OrgRole.valueOf(role.toUpperCase()); } catch (Exception ignored) {}
        Optional<OrgInvitation> existing = inviteRepo.findByEmailAndOrgId(email, orgId);
        if (existing.isPresent()) return ResponseEntity.ok(Map.of("status", "already_sent"));
        OrgInvitation invite = new OrgInvitation(orgId, email, orgRole);
        inviteRepo.save(invite);
        return ResponseEntity.ok(Map.of("status", "invited", "token", invite.getToken()));
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<Map<String, Object>> acceptInvite(@RequestParam String token, @RequestParam String userId) {
        Optional<OrgInvitation> inviteOpt = inviteRepo.findByToken(token);
        if (inviteOpt.isEmpty()) return ResponseEntity.notFound().build();
        OrgInvitation invite = inviteOpt.get();
        if (invite.isAccepted() || invite.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.badRequest().build();
        }
        OrganizationMember member = new OrganizationMember(invite.getOrgId(), userId, invite.getRole());
        memberRepo.save(member);
        invite.setAccepted(true);
        inviteRepo.save(invite);
        return ResponseEntity.ok(Map.of("status", "joined", "orgId", invite.getOrgId()));
    }

    @PostMapping("/invite/revoke")
    public ResponseEntity<Map<String, Object>> revokeInvite(@RequestParam Long inviteId) {
        inviteRepo.deleteById(inviteId);
        return ResponseEntity.ok(Map.of("status", "revoked"));
    }

    @GetMapping("/members")
    public ResponseEntity<List<Map<String, Object>>> getMembers(@RequestParam Long orgId) {
        List<OrganizationMember> members = memberRepo.findByOrgId(orgId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (OrganizationMember m : members) {
            result.add(Map.of("userId", m.getUserId(), "role", m.getRole().toString()));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/invitations")
    public ResponseEntity<List<Map<String, Object>>> getInvitations(@RequestParam Long orgId) {
        List<OrgInvitation> invites = inviteRepo.findByOrgId(orgId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (OrgInvitation i : invites) {
            result.add(Map.of("id", i.getId(), "email", i.getEmail(), "role", i.getRole().toString(), "accepted", i.isAccepted()));
        }
        return ResponseEntity.ok(result);
    }

    // Metadata endpoints
    @PostMapping("/{orgId}/metadata/public")
    public ResponseEntity<Map<String, Object>> setPublicMetadata(@PathVariable Long orgId, @RequestBody Map<String, String> metadata) {
        Organization org = orgRepo.findById(orgId).orElse(null);
        if (org == null) return ResponseEntity.notFound().build();
        org.setPublicMetadata(metadata);
        orgRepo.save(org);
        return ResponseEntity.ok(Map.of("status", "saved"));
    }

    @PostMapping("/{orgId}/metadata/private")
    public ResponseEntity<Map<String, Object>> setPrivateMetadata(@PathVariable Long orgId, @RequestBody Map<String, String> metadata) {
        Organization org = orgRepo.findById(orgId).orElse(null);
        if (org == null) return ResponseEntity.notFound().build();
        org.setPrivateMetadata(metadata);
        orgRepo.save(org);
        return ResponseEntity.ok(Map.of("status", "saved"));
    }

    @GetMapping("/{orgId}/metadata/public")
    public ResponseEntity<Map<String, String>> getPublicMetadata(@PathVariable Long orgId) {
        Organization org = orgRepo.findById(orgId).orElse(null);
        if (org == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(org.getPublicMetadata());
    }

    // Check role/permission
    @PostMapping("/check-access")
    public ResponseEntity<Map<String, Object>> checkAccess(@RequestParam Long orgId, @RequestParam String userId, @RequestParam String permission) {
        Optional<OrganizationMember> m = memberRepo.findByOrgIdAndUserId(orgId, userId);
        if (m.isEmpty()) return ResponseEntity.status(403).build();
        OrgRole role = m.get().getRole();
        boolean hasAccess = switch (permission) {
            case "manage_members" -> role == OrgRole.ADMIN;
            case "read_billing" -> role == OrgRole.ADMIN;
            case "manage_billing" -> role == OrgRole.ADMIN;
            default -> role == OrgRole.ADMIN || role == OrgRole.MEMBER;
        };
        return ResponseEntity.ok(Map.of("granted", hasAccess, "role", role.toString()));
    }
}