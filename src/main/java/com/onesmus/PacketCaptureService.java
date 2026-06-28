package com.onesmus;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class PacketCaptureService {

    private final SessionManager sessionManager;

    private volatile boolean capturing = false;
    private Thread captureThread;
    private String activeInterface;

    public PacketCaptureService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public List<String> getAvailableInterfaces() {
        try {
            return Pcaps.findAllDevs().stream()
                    .map(PcapNetworkInterface::getName)
                    .collect(Collectors.toList());
        } catch (PcapNativeException e) {
            return List.of();
        }
    }

    public void startCapture(String interfaceName) {
        if (capturing) return;
        activeInterface = interfaceName != null ? interfaceName : detectDefaultInterface();
        if (activeInterface == null) {
            System.out.println("❌ No network interface found");
            return;
        }
        capturing = true;
        captureThread = new Thread(this::capturePackets);
        captureThread.setDaemon(true);
        captureThread.start();
        System.out.println("✓ Packet capture started on " + activeInterface);
    }

    public void startCapture() {
        startCapture(null);
    }

    public void stopCapture() {
        capturing = false;
        if (captureThread != null) {
            try {
                captureThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("✗ Packet capture stopped");
    }

    private String detectDefaultInterface() {
        try {
            List<PcapNetworkInterface> devs = Pcaps.findAllDevs();
            // Prefer interfaces that are up and not loopback
            return devs.stream()
                    .filter(d -> !d.isLoopBack() && !d.getAddresses().isEmpty())
                    .map(PcapNetworkInterface::getName)
                    .findFirst()
                    .orElseGet(() -> devs.isEmpty() ? null : devs.get(0).getName());
        } catch (PcapNativeException e) {
            return null;
        }
    }

    private void capturePackets() {
        try {
            var nif = Pcaps.getDevByName(activeInterface);
            if (nif == null) {
                System.out.println("❌ Interface " + activeInterface + " not found");
                capturing = false;
                return;
            }

            var handle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);

            while (capturing) {
                Packet packet = handle.getNextPacket();
                if (packet != null) processPacket(packet);
            }

            handle.close();
        } catch (Exception e) {
            System.err.println("Packet capture error: " + e.getMessage());
            capturing = false;
        }
    }

    private void processPacket(Packet packet) {
        var ipPacket = packet.get(IpV4Packet.class);
        if (ipPacket == null) return;

        String srcIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipPacket.getHeader().getDstAddr().getHostAddress();
        int len = packet.length();

        String protocol = "IPv4";
        int srcPort = 0;
        int dstPort = 0;

        var tcpPacket = packet.get(TcpPacket.class);
        if (tcpPacket != null) {
            protocol = getApplicationProtocol(tcpPacket.getHeader().getDstPort().valueAsInt());
            srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
            dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
        } else {
            var udpPacket = packet.get(UdpPacket.class);
            if (udpPacket != null) {
                protocol = getApplicationProtocol(udpPacket.getHeader().getDstPort().valueAsInt());
                srcPort = udpPacket.getHeader().getSrcPort().valueAsInt();
                dstPort = udpPacket.getHeader().getDstPort().valueAsInt();
            }
        }

        PacketData data = new PacketData(protocol, srcIp, srcPort, dstIp, dstPort, len);
        
        // Broadcast to relevant sessions
        sessionManager.getAllSessions().forEach((username, session) -> {
            String userIp = sessionManager.getUserIp(username);
            // Admin sees everything, or if IP matches
            if (userIp == null || srcIp.equals(userIp) || dstIp.equals(userIp)) {
                session.addPacket(data);
            }
        });
        sessionManager.getGlobalSession().addPacket(data);
    }

    private void addPacket(PacketData data) {
        // Method removed as sessions handle adding
    }

    private String getApplicationProtocol(int port) {
        return switch (port) {
            case 80      -> "HTTP";
            case 443     -> "HTTPS";
            case 53      -> "DNS";
            case 67, 68  -> "DHCP";
            case 22      -> "SSH";
            case 21      -> "FTP";
            case 25      -> "SMTP";
            case 110     -> "POP3";
            case 143     -> "IMAP";
            default      -> "UNKNOWN";
        };
    }

    public boolean isCapturing() { return capturing; }

    public String getActiveInterface() { return activeInterface; }
}
