package com.onesmus;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PacketController {

    private final PacketCaptureService capture;
    private final CarlosService carlos;
    private final SessionManager sessions;

    @Value("${netpulse.android.apk-path:}")
    private String androidApkPath;

    public PacketController(PacketCaptureService capture, CarlosService carlos, SessionManager sessions) {
        this.capture = capture;
        this.carlos = carlos;
        this.sessions = sessions;
    }

    @GetMapping("/interfaces")
    public Map<String, Object> interfaces() {
        return Map.of("interfaces", capture.getAvailableInterfaces(),
                "active", capture.getActiveInterface() != null ? capture.getActiveInterface() : "");
    }

    @GetMapping("/packets")
    public Map<String, Object> packets(Principal user, @RequestParam(defaultValue = "50") int limit) {
        var s = sessions.get(user.getName());
        var list = s.recent(limit);
        return Map.of("packets", list, "count", list.size(), "timestamp", System.currentTimeMillis());
    }

    @GetMapping("/statistics")
    public Map<String, Object> stats(Principal user) {
        var s = sessions.get(user.getName()).stats();
        Map<String, Object> r = new HashMap<>();
        r.put("totalPackets", s.getTotalPackets());
        r.put("tcpPackets", s.getTcpPackets());
        r.put("udpPackets", s.getUdpPackets());
        r.put("otherPackets", s.getOtherPackets());
        r.put("topTalkers", s.getTopTalkers());
        r.put("timestamp", System.currentTimeMillis());
        return r;
    }

    @GetMapping("/admin/all-stats")
    public Map<String, Object> allStats() {
        Map<String, Object> r = new HashMap<>();
        sessions.all().forEach((u, s) -> r.put(u, s.stats().getTotalPackets()));
        return r;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("capturing", capture.isCapturing(),
                "interface", capture.getActiveInterface() != null ? capture.getActiveInterface() : "",
                "timestamp", System.currentTimeMillis());
    }

    @PostMapping("/start")
    public Map<String, String> start(@RequestParam(required = false) String iface) {
        capture.start(iface);
        return Map.of("status", "started", "message", "Capture started on " +
                (capture.getActiveInterface() != null ? capture.getActiveInterface() : "unknown"));
    }

    @PostMapping("/stop")
    public Map<String, String> stop() {
        capture.stop();
        return Map.of("status", "stopped", "message", "Capture stopped");
    }

    @GetMapping("/download/android")
    public ResponseEntity<Resource> downloadApk() {
        String apkPath = androidApkPath.isBlank()
            ? "/home/onesmus/IdeaProjects/NetPulseAndroid/app/build/outputs/apk/debug/app-debug.apk"
            : androidApkPath;
        File apk = new File(apkPath);
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
        String apkPath = androidApkPath.isBlank()
            ? "/home/onesmus/IdeaProjects/NetPulseAndroid/app/build/outputs/apk/debug/app-debug.apk"
            : androidApkPath;
        File apk = new File(apkPath);
        return Map.of("available", apk.exists(), "size", apk.exists() ? apk.length() : 0);
    }

    @PostMapping("/carlos/chat")
    public Map<String, String> chat(Principal user, @RequestBody Map<String, String> body) {
        String msg = body.getOrDefault("message", "").trim();
        if (msg.isEmpty()) return Map.of("reply", "Please ask me something!");

        var session = sessions.get(user.getName());
        return Map.of("reply", carlos.chat(msg, session.stats(), session.recent(50)));
    }

    @GetMapping("/carlos/status")
    public Map<String, Object> carlosStatus() {
        return Map.of("configured", carlos.hasApiKey());
    }
}