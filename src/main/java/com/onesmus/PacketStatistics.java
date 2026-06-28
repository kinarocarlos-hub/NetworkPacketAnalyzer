package com.onesmus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class PacketStatistics {

    private final AtomicLong totalPackets = new AtomicLong();
    private final AtomicLong tcpPackets = new AtomicLong();
    private final AtomicLong udpPackets = new AtomicLong();
    private final AtomicLong otherPackets = new AtomicLong();
    private Map<String, Integer> topTalkers = new LinkedHashMap<>();

    public long getTotalPackets() { return totalPackets.get(); }
    public long getTcpPackets()   { return tcpPackets.get(); }
    public long getUdpPackets()   { return udpPackets.get(); }
    public long getOtherPackets() { return otherPackets.get(); }

    public Map<String, Integer> getTopTalkers() { return topTalkers; }
    public void setTopTalkers(Map<String, Integer> topTalkers) { this.topTalkers = topTalkers; }

    public void incrementTotalPackets() { totalPackets.incrementAndGet(); }
    public void incrementTcpPackets()   { tcpPackets.incrementAndGet(); }
    public void incrementUdpPackets()   { udpPackets.incrementAndGet(); }
    public void incrementOtherPackets() { otherPackets.incrementAndGet(); }
}
