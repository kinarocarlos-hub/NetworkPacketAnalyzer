package com.onesmus.vpn;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class WireGuardAdminService {

    private static final String WG_CONFIG = "/etc/wireguard/wg0.conf";
    private static final String WG_IFACE = "wg0";

    private final Map<String, PeerConfig> activePeers = new LinkedHashMap<>();

    public void addPeer(String peerId, String publicKey, String ipAddress) {
        activePeers.put(peerId, new PeerConfig(publicKey, ipAddress));
        reloadConfig();
    }

    public void removePeer(String peerId) {
        activePeers.remove(peerId);
        reloadConfig();
    }

    // Calls wg syncconf to push peer changes without tearing down the interface
    private void reloadConfig() {
        try {
            Process proc = new ProcessBuilder("wg", "syncconf", WG_IFACE).start();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
            for (String line : buildConfigLines()) {
                out.write(line);
                out.newLine();
            }
            out.flush();

            int exit = proc.waitFor();
            if (exit != 0) {
                throw new RuntimeException("wg syncconf failed (exit " + exit + ")");
            }
        } catch (Exception err) {
            // This is genuinely problematic - we can't update the VPN config
            throw new RuntimeException("Failed to apply WireGuard config", err);
        }
    }

    // Builds config for wg syncconf interface
    private List<String> buildConfigLines() {
        List<String> lines = new ArrayList<>();
        lines.add("[Interface]");
        lines.add("Address = 10.0.0.1/24");
        lines.add("ListenPort = 51820");
        lines.add("PrivateKey = <private-key>");

        for (PeerConfig pc : activePeers.values()) {
            lines.add("");
            lines.add("[Peer]");
            lines.add("PublicKey = " + pc.publicKey);
            lines.add("AllowedIPs = " + pc.ip + "/32");
        }
        return lines;
    }

    public Map<String, Object> getNodeStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("interface", WG_IFACE);
        status.put("peers", activePeers.size());
        status.put("status", "running");
        return status;
    }

    private record PeerConfig(String publicKey, String ip) {}
}