package com.onesmus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PacketController {

    @Autowired
    private PacketCaptureService captureService;

    @GetMapping("/packets")
    public Map<String, Object> getRecentPackets(
            @RequestParam(defaultValue = "50") int limit) {
        Map<String, Object> response = new HashMap<>();
        response.put("packets", captureService.getRecentPackets(limit));
        response.put("count", captureService.getRecentPackets(limit).size());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        PacketStatistics stats = captureService.getStatistics();
        Map<String, Object> response = new HashMap<>();
        response.put("totalPackets", stats.getTotalPackets());
        response.put("tcpPackets", stats.getTcpPackets());
        response.put("udpPackets", stats.getUdpPackets());
        response.put("otherPackets", stats.getOtherPackets());
        response.put("topTalkers", stats.getTopTalkers());
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("capturing", captureService.isCapturing());
        response.put("interface", "wlan0");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @PostMapping("/start")
    public Map<String, String> startCapture() {
        captureService.startCapture();
        Map<String, String> response = new HashMap<>();
        response.put("status", "started");
        response.put("message", "Packet capture started");
        return response;
    }

    @PostMapping("/stop")
    public Map<String, String> stopCapture() {
        captureService.stopCapture();
        Map<String, String> response = new HashMap<>();
        response.put("status", "stopped");
        response.put("message", "Packet capture stopped");
        return response;
    }
}
