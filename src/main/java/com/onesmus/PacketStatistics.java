package com.onesmus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class PacketStatistics {

    private final AtomicLong total = new AtomicLong();
    private final AtomicLong tcp = new AtomicLong();
    private final AtomicLong udp = new AtomicLong();
    private final AtomicLong other = new AtomicLong();
    private Map<String, Integer> topTalkers = new LinkedHashMap<>();

    public long getTotalPackets() { return total.get(); }
    public long getTcpPackets() { return tcp.get(); }
    public long getUdpPackets() { return udp.get(); }
    public long getOtherPackets() { return other.get(); }

    public Map<String, Integer> getTopTalkers() { return topTalkers; }
    public void setTopTalkers(Map<String, Integer> topTalkers) { this.topTalkers = topTalkers; }

    public void recordPacket(String ip, boolean isTcp, boolean isUdp) {
        total.incrementAndGet();
        if (isTcp) tcp.incrementAndGet();
        else if (isUdp) udp.incrementAndGet();
        else other.incrementAndGet();
    }
}
