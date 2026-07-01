package com.onesmus;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class PacketCaptureService {

    private final SessionManager sessions;

    private volatile boolean running = false;
    private Thread thread;
    private String iface;

    public PacketCaptureService(SessionManager sessions) {
        this.sessions = sessions;
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

    public void start(String iface) {
        if (running) return;
        this.iface = (iface != null) ? iface : autoDetectInterface();
        if (this.iface == null) {
            System.err.println("No suitable network interface found");
            return;
        }
        running = true;
        thread = new Thread(this::loop, "packet-capture");
        thread.setDaemon(true);
        thread.start();
        System.out.println("Capture started on " + this.iface);
    }

    public void start() {
        start(null);
    }

    public void stop() {
        running = false;
        if (thread != null) {
            try {
                thread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Capture stopped");
    }

    private String autoDetectInterface() {
        try {
            var devs = Pcaps.findAllDevs();
            return devs.stream()
                    .filter(d -> !d.isLoopBack() && !d.getAddresses().isEmpty())
                    .map(PcapNetworkInterface::getName)
                    .findFirst()
                    .orElseGet(() -> devs.isEmpty() ? null : devs.get(0).getName());
        } catch (PcapNativeException e) {
            return null;
        }
    }

    private void loop() {
        while (running) {
            try {
                var dev = Pcaps.getDevByName(iface);
                if (dev == null) {
                    System.err.println("Interface " + iface + " disappeared");
                    running = false;
                    return;
                }
                var handle = dev.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);

                while (running) {
                    Packet pkt = handle.getNextPacket();
                    if (pkt != null) handlePacket(pkt);
                }
                handle.close();
            } catch (PcapNativeException e) {
                System.err.println("Capture error: " + e.getMessage());
                if (running) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            } catch (Exception e) {
                System.err.println("Capture error: " + e.getMessage());
                running = false;
            }
        }
    }

    private void handlePacket(Packet pkt) {
        var ip = pkt.get(IpV4Packet.class);
        if (ip == null) return;

        String src = ip.getHeader().getSrcAddr().getHostAddress();
        String dst = ip.getHeader().getDstAddr().getHostAddress();
        int len = pkt.length();

        String proto = "IPv4";
        int srcPort = 0, dstPort = 0;

        var tcp = pkt.get(TcpPacket.class);
        if (tcp != null) {
            proto = appProto(dstPort = tcp.getHeader().getDstPort().valueAsInt());
            srcPort = tcp.getHeader().getSrcPort().valueAsInt();
        } else {
            var udp = pkt.get(UdpPacket.class);
            if (udp != null) {
                proto = appProto(dstPort = udp.getHeader().getDstPort().valueAsInt());
                srcPort = udp.getHeader().getSrcPort().valueAsInt();
            }
        }

        var data = new PacketData(proto, src, srcPort, dst, dstPort, len);

        // Broadcast to all sessions (in prod would filter by user IP)
        sessions.all().values().forEach(s -> s.add(data));
    }

    String appProto(int port) {
        return switch (port) {
            case 80 -> "HTTP";
            case 443 -> "HTTPS";
            case 53 -> "DNS";
            case 67, 68 -> "DHCP";
            case 22 -> "SSH";
            case 21 -> "FTP";
            case 25 -> "SMTP";
            case 110 -> "POP3";
            case 143 -> "IMAP";
            default -> "UNKNOWN";
        };
    }

    public boolean isCapturing() { return running; }
    public String getActiveInterface() { return iface; }
}
