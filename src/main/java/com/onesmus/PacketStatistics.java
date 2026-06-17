package com.onesmus;

import java.util.*;

public class PacketStatistics {
    private long totalPackets;
    private long tcpPackets;
    private long udpPackets;
    private long otherPackets;
    private Map<String, Integer> topTalkers;

    public PacketStatistics() {
        this.totalPackets = 0;
        this.tcpPackets = 0;
        this.udpPackets = 0;
        this.otherPackets = 0;
        this.topTalkers = new LinkedHashMap<>();
    }

    public long getTotalPackets() {
        return totalPackets;
    }

    public void setTotalPackets(long totalPackets) {
        this.totalPackets = totalPackets;
    }

    public long getTcpPackets() {
        return tcpPackets;
    }

    public void setTcpPackets(long tcpPackets) {
        this.tcpPackets = tcpPackets;
    }

    public long getUdpPackets() {
        return udpPackets;
    }

    public void setUdpPackets(long udpPackets) {
        this.udpPackets = udpPackets;
    }

    public long getOtherPackets() {
        return otherPackets;
    }

    public void setOtherPackets(long otherPackets) {
        this.otherPackets = otherPackets;
    }

    public Map<String, Integer> getTopTalkers() {
        return topTalkers;
    }

    public void setTopTalkers(Map<String, Integer> topTalkers) {
        this.topTalkers = topTalkers;
    }

    public void incrementTotalPackets() {
        this.totalPackets++;
    }

    public void incrementTcpPackets() {
        this.tcpPackets++;
    }

    public void incrementUdpPackets() {
        this.udpPackets++;
    }

    public void incrementOtherPackets() {
        this.otherPackets++;
    }
}
