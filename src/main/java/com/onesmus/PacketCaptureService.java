package com.onesmus;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class PacketCaptureService {

    private static final int BUFFER_SIZE = 1000;
    private static final int TOP_TALKERS_LIMIT = 10;

    private final BlockingQueue<PacketData> packetQueue = new LinkedBlockingQueue<>(BUFFER_SIZE);
    private final PacketStatistics statistics = new PacketStatistics();
    private final Map<String, Integer> sourceIpStats = new ConcurrentHashMap<>();

    private volatile boolean capturing = false;
    private Thread captureThread;

    public void startCapture() {
        if (capturing) {
            return;
        }

        capturing = true;
        captureThread = new Thread(this::capturePackets);
        captureThread.setDaemon(true);
        captureThread.start();
        System.out.println("✓ Packet capture started on wlan0");
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

    private void capturePackets() {
        try {
            PcapNetworkInterface nif = Pcaps.getDevByName("wlan0");
            if (nif == null) {
                System.out.println("❌ Interface wlan0 not found");
                capturing = false;
                return;
            }

            PcapHandle handle = nif.openLive(
                    65536,
                    PcapNetworkInterface.PromiscuousMode.PROMISCUOUS,
                    10);

            while (capturing) {
                Packet packet = handle.getNextPacket();

                if (packet == null) {
                    continue;
                }

                processPacket(packet);
            }

            handle.close();
        } catch (Exception e) {
            System.err.println("Packet capture error: " + e.getMessage());
            capturing = false;
        }
    }

    private void processPacket(Packet packet) {
        statistics.incrementTotalPackets();

        IpV4Packet ipPacket = packet.get(IpV4Packet.class);
        if (ipPacket == null) {
            return;
        }

        String srcIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipPacket.getHeader().getDstAddr().getHostAddress();
        int packetLength = packet.length();

        sourceIpStats.put(srcIp, sourceIpStats.getOrDefault(srcIp, 0) + 1);

        TcpPacket tcpPacket = packet.get(TcpPacket.class);
        if (tcpPacket != null) {
            statistics.incrementTcpPackets();
            int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
            int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
            String protocol = getApplicationProtocol(dstPort);

            PacketData data = new PacketData(protocol, srcIp, srcPort,
                    dstIp, dstPort, packetLength);
            addPacket(data);
        } else {
            UdpPacket udpPacket = packet.get(UdpPacket.class);
            if (udpPacket != null) {
                statistics.incrementUdpPackets();
                int srcPort = udpPacket.getHeader().getSrcPort().valueAsInt();
                int dstPort = udpPacket.getHeader().getDstPort().valueAsInt();
                String protocol = getApplicationProtocol(dstPort);

                PacketData data = new PacketData(protocol, srcIp, srcPort,
                        dstIp, dstPort, packetLength);
                addPacket(data);
            } else {
                statistics.incrementOtherPackets();
                PacketData data = new PacketData("IPv4", srcIp, 0,
                        dstIp, 0, packetLength);
                addPacket(data);
            }
        }
    }

    private void addPacket(PacketData data) {
        if (!packetQueue.offer(data)) {
            packetQueue.poll();
            packetQueue.offer(data);
        }
        System.out.println(data);
    }

    private String getApplicationProtocol(int port) {
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

    public List<PacketData> getRecentPackets(int limit) {
        List<PacketData> packets = new ArrayList<>(packetQueue);
        return packets.subList(Math.max(0, packets.size() - limit), packets.size());
    }

    public PacketStatistics getStatistics() {
        statistics.setTopTalkers(getTopTalkers());
        return statistics;
    }

    private Map<String, Integer> getTopTalkers() {
        return sourceIpStats.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(TOP_TALKERS_LIMIT)
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll);
    }

    public boolean isCapturing() {
        return capturing;
    }
}
