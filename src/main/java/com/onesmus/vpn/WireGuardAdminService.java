package com.onesmus.vpn;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

@Service
public class WireGuardAdminService {
    private final Map<String, PeerConfig> activePeers = new LinkedHashMap<>();
    private final String WG_CONFIG_PATH = "/etc/wireguard/wg0.conf";
    private final String WG_INTERFACE = "wg0";
    
    public void addPeer(String peerId, String publicKey, String ipAddress) {
        activePeers.put(peerId, new PeerConfig(publicKey, ipAddress));
        
        try {
            applyConfig();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply WireGuard config", e);
        }
    }
    
    public void removePeer(String peerId) {
        activePeers.remove(peerId);
        
        try {
            applyConfig();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply WireGuard config", e);
        }
    }
    
    private void applyConfig() throws IOException, InterruptedException {
        List<String> configLines = buildConfig();
        
        ProcessBuilder pb = new ProcessBuilder("wg", "syncconf", WG_INTERFACE);
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream()))) {
            for (String line : configLines) {
                writer.write(line);
                writer.newLine();
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("wg syncconf failed with exit code " + exitCode);
        }
    }
    
    private List<String> buildConfig() {
        List<String> lines = new ArrayList<>();
        lines.add("[Interface]");
        lines.add("Address = 10.0.0.1/24");
        lines.add("ListenPort = 51820");
        lines.add("PrivateKey = <private-key>");
        
        for (Map.Entry<String, PeerConfig> entry : activePeers.entrySet()) {
            PeerConfig peer = entry.getValue();
            lines.add("");
            lines.add("[Peer]");
            lines.add("PublicKey = " + peer.publicKey());
            lines.add("AllowedIPs = " + peer.ipAddress() + "/32");
        }
        
        return lines;
    }
    
    public Map<String, Object> getNodeStatus() {
        return Map.of(
                "interface", WG_INTERFACE,
                "peers", activePeers.size(),
                "status", "running"
        );
    }
    
    private record PeerConfig(String publicKey, String ipAddress) {}
}