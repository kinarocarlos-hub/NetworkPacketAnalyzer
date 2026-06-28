package com.onesmus;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class StatisticsManager {

    private int totalPackets;
    private int tcpPackets;
    private int udpPackets;

    private final Map<String, Integer> talkers = new HashMap<>();

    public void recordPacket(String sourceIp, String protocol) {
        totalPackets++;
        talkers.merge(sourceIp, 1, Integer::sum);
        if ("TCP".equals(protocol)) tcpPackets++;
        if ("UDP".equals(protocol)) udpPackets++;
    }

    public void printStatistics() {
        System.out.println("\n===== STATISTICS =====");
        System.out.println("Total: " + totalPackets);
        System.out.println("TCP:   " + tcpPackets);
        System.out.println("UDP:   " + udpPackets);
        System.out.println("\nTop Talkers:");
        talkers.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry<String, Integer>::getValue).reversed())
                .limit(5)
                .forEach(System.out::println);
    }

    public int getTotalPackets() {
        return totalPackets;
    }
}
