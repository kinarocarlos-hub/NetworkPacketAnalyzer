package com.onesmus;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PacketController {

    private final PacketCaptureService captureService;
    private final CarlosService carlosService;
    private final SessionManager sessionManager;

    public PacketController(PacketCaptureService captureService, CarlosService carlosService, SessionManager sessionManager) {
        this.captureService = captureService;
        this.carlosService  = carlosService;
        this.sessionManager = sessionManager;
    }

    @GetMapping("/interfaces")
    public Map<String, Object> getInterfaces() {
        return Map.of(
                "interfaces", captureService.getAvailableInterfaces(),
                "active", captureService.getActiveInterface() != null ? captureService.getActiveInterface() : ""
        );
    }

    @GetMapping("/packets")
    public Map<String, Object> getRecentPackets(@AuthenticationPrincipal UserDetails user, @RequestParam(defaultValue = "50") int limit) {
        UserSessionData session = sessionManager.getSession(user.getUsername());
        var packets = session.getRecentPackets(limit);
        return Map.of("packets", packets, "count", packets.size(), "timestamp", System.currentTimeMillis());
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics(@AuthenticationPrincipal UserDetails user) {
        UserSessionData session = sessionManager.getSession(user.getUsername());
        PacketStatistics stats = session.getStatistics();
        Map<String, Object> response = new HashMap<>();
        response.put("totalPackets", stats.getTotalPackets());
        response.put("tcpPackets", stats.getTcpPackets());
        response.put("udpPackets", stats.getUdpPackets());
        response.put("otherPackets", stats.getOtherPackets());
        response.put("topTalkers", stats.getTopTalkers());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/admin/all-stats")
    public Map<String, Object> getAllStats() {
        Map<String, Object> response = new HashMap<>();
        sessionManager.getAllSessions().forEach((user, session) -> {
            response.put(user, session.getStatistics().getTotalPackets());
        });
        return response;
    }

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        return Map.of(
                "username", user.getUsername(),
                "roles", user.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        );
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        return Map.of(
                "capturing", captureService.isCapturing(),
                "interface", captureService.getActiveInterface() != null ? captureService.getActiveInterface() : "",
                "timestamp", System.currentTimeMillis()
        );
    }

    @PostMapping("/start")
    public Map<String, String> startCapture(@RequestParam(required = false) String iface) {
        captureService.startCapture(iface);
        return Map.of("status", "started", "message", "Packet capture started on " +
                (captureService.getActiveInterface() != null ? captureService.getActiveInterface() : "unknown"));
    }

    @PostMapping("/stop")
    public Map<String, String> stopCapture() {
        captureService.stopCapture();
        return Map.of("status", "stopped", "message", "Packet capture stopped");
    }

    @GetMapping("/download/android")
    public ResponseEntity<Resource> downloadAndroid() {
        File apk = new File("/home/onesmus/IdeaProjects/NetPulseAndroid/app/build/outputs/apk/debug/app-debug.apk");
        if (!apk.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=NetPulse.apk")
                .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
                .contentLength(apk.length())
                .body(new FileSystemResource(apk));
    }

    @GetMapping("/download/status")
    public Map<String, Object> downloadStatus() {
        File apk = new File("/home/onesmus/IdeaProjects/NetPulseAndroid/app/build/outputs/apk/debug/app-debug.apk");
        return Map.of("available", apk.exists(), "size", apk.exists() ? apk.length() : 0);
    }

    @PostMapping("/carlos/chat")
    public Map<String, String> carlosChat(@AuthenticationPrincipal UserDetails user, @RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "").trim();
        if (message.isEmpty()) return Map.of("reply", "Please ask me something!");
        
        UserSessionData session = sessionManager.getSession(user.getUsername());
        String reply = carlosService.chat(message,
                session.getStatistics(), session.getRecentPackets(50));
        return Map.of("reply", reply);
    }

    @GetMapping("/carlos/status")
    public Map<String, Object> carlosStatus() {
        return Map.of("configured", carlosService.hasApiKey());
    }
}
