package com.onesmus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class UserSessionData {
    private static final int BUFFER_SIZE = 1000;
    
    private final BlockingQueue<PacketData> packetQueue = new LinkedBlockingQueue<>(BUFFER_SIZE);
    private final PacketStatistics statistics = new PacketStatistics();
    private final Map<String, Integer> sourceIpStats = new ConcurrentHashMap<>();

    public void addPacket(PacketData data) {
        if (!packetQueue.offer(data)) {
            packetQueue.poll();
            packetQueue.offer(data);
        }
        statistics.incrementTotalPackets();
        // Update stats based on protocol
        if ("TCP".equals(data.getProtocol())) statistics.incrementTcpPackets();
        else if ("UDP".equals(data.getProtocol())) statistics.incrementUdpPackets();
        else statistics.incrementOtherPackets();
        
        sourceIpStats.merge(data.getSourceIp(), 1, Integer::sum);
    }

    public List<PacketData> getRecentPackets(int limit) {
        var packets = new ArrayList<>(packetQueue);
        return packets.subList(Math.max(0, packets.size() - limit), packets.size());
    }

    public PacketStatistics getStatistics() {
        // Top talkers calculation would go here or be updated on add
        return statistics;
    }

    public Map<String, Integer> getSourceIpStats() {
        return sourceIpStats;
    }
}
